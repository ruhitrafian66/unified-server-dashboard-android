package com.unifiedserver.dashboard.data.repository

import android.content.Context
import okhttp3.Call
import okhttp3.Dns
import okhttp3.EventListener
import okhttp3.Protocol
import java.io.IOException
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** Where remembered addresses are kept between app starts. */
interface AddressStore {
    fun load(host: String): List<InetAddress>
    /** An empty list forgets the host. */
    fun save(host: String, addresses: List<InetAddress>)

    object None : AddressStore {
        override fun load(host: String) = emptyList<InetAddress>()
        override fun save(host: String, addresses: List<InetAddress>) = Unit
    }
}

/**
 * Resolving an mDNS name such as dash.local takes about 0.8 s on Android, and the system
 * does not cache the answer, so every new connection to the home server waited that long.
 *
 * For `.local` names this answers at once with the last address that resolved and looks
 * the name up again in the background. A newer answer replaces the remembered one. A
 * lookup that fails changes nothing: mDNS answers are easily lost while the server is
 * fine, and the address still works.
 *
 * The address is forgotten only when connecting to it fails ([forget], called by the
 * HTTP client). The next request then looks the name up afresh, which finds a server
 * that moved and, away from home, shows the real "unknown host" error.
 *
 * Every other name goes straight to the system resolver, which caches properly.
 */
class LocalNameDns(
    private val system: Dns = Dns.SYSTEM,
    private val background: Executor = Executors.newSingleThreadExecutor { Thread(it, "local-dns").apply { isDaemon = true } },
    private val clock: () -> Long = System::currentTimeMillis
) : Dns {
    /** Set once at app start, before the first request. */
    @Volatile var store: AddressStore = AddressStore.None

    private val known = ConcurrentHashMap<String, List<InetAddress>>()
    private val refreshedAt = ConcurrentHashMap<String, Long>()

    override fun lookup(hostname: String): List<InetAddress> {
        val host = hostname.lowercase()
        if (!host.endsWith(".local")) return system.lookup(hostname)
        val remembered = known[host] ?: store.load(host).takeIf { it.isNotEmpty() }?.also { known[host] = it }
        if (remembered == null) return resolve(host)
        refreshLater(host)
        return remembered
    }

    private fun resolve(host: String): List<InetAddress> =
        system.lookup(host).also {
            known[host] = it
            refreshedAt[host] = clock()
            store.save(host, it)
        }

    /** One background lookup per host at a time, and not more often than [REFRESH_MS]. */
    private fun refreshLater(host: String) {
        val now = clock()
        val last = refreshedAt[host]
        if (last != null && now - last < REFRESH_MS) return
        if (last == null) { if (refreshedAt.putIfAbsent(host, now) != null) return }
        else if (!refreshedAt.replace(host, last, now)) return
        background.execute {
            // A failed lookup keeps the remembered address; see the class comment.
            try { resolve(host) } catch (_: Exception) { }
        }
    }

    /** A connection to [hostname] could not be opened: stop trusting what is remembered. */
    fun forget(hostname: String) {
        val host = hostname.lowercase()
        if (!host.endsWith(".local")) return
        known.remove(host)
        refreshedAt.remove(host)
        store.save(host, emptyList())
    }

    private companion object {
        const val REFRESH_MS = 60_000L
    }
}

/** Tells [dns] when a connection could not be opened, so a stale address is not reused. */
class ForgetOnConnectFailure(private val dns: LocalNameDns) : EventListener() {
    override fun connectFailed(
        call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException
    ) {
        // Cancelling a request closes its socket mid-connect; that says nothing about the address.
        if (!call.isCanceled()) dns.forget(call.request().url.host)
    }
}

/**
 * Addresses as text in SharedPreferences: [Dns.lookup] is a blocking call, so this needs
 * a synchronous read. Link-local IPv6 addresses are skipped; they only work with the
 * network interface they were resolved on, which is not worth storing.
 */
class PreferencesAddressStore(context: Context) : AddressStore {
    private val prefs = context.getSharedPreferences("local_name_addresses", Context.MODE_PRIVATE)

    override fun load(host: String): List<InetAddress> =
        prefs.getStringSet(host, null).orEmpty().mapNotNull { literal ->
            // A literal never triggers a lookup; keep the name so logs and TLS still see it.
            runCatching { InetAddress.getByAddress(host, InetAddress.getByName(literal).address) }.getOrNull()
        }

    override fun save(host: String, addresses: List<InetAddress>) {
        val literals = addresses.filterNot { it is Inet6Address && it.isLinkLocalAddress }
            .mapNotNull { it.hostAddress }.toSet()
        prefs.edit().apply { if (literals.isEmpty()) remove(host) else putStringSet(host, literals) }.apply()
    }
}
