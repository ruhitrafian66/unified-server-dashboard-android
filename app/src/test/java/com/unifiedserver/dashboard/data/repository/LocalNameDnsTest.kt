package com.unifiedserver.dashboard.data.repository

import okhttp3.Call
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.concurrent.Executor

class LocalNameDnsTest {
    private fun ip(a: Int, b: Int, c: Int, d: Int) =
        InetAddress.getByAddress(byteArrayOf(a.toByte(), b.toByte(), c.toByte(), d.toByte()))

    /** A resolver whose answer the test can change, counting how often it is asked. */
    private class Resolver(var answer: List<InetAddress>?) : Dns {
        val asked = mutableListOf<String>()
        override fun lookup(hostname: String): List<InetAddress> {
            asked += hostname
            return answer ?: throw UnknownHostException(hostname)
        }
    }

    private class Memory : AddressStore {
        val saved = mutableMapOf<String, List<InetAddress>>()
        override fun load(host: String) = saved[host].orEmpty()
        override fun save(host: String, addresses: List<InetAddress>) {
            if (addresses.isEmpty()) saved.remove(host) else saved[host] = addresses
        }
    }

    /** Background lookups wait here until the test runs them. */
    private class Later : Executor {
        val queued = mutableListOf<Runnable>()
        override fun execute(command: Runnable) { queued += command }
        fun run() { queued.toList().also { queued.clear() }.forEach { it.run() } }
    }

    private val home = ip(192, 168, 0, 30)
    private val moved = ip(192, 168, 0, 31)
    private var now = 0L

    private fun dns(resolver: Resolver, later: Later, store: AddressStore = Memory()) =
        LocalNameDns(resolver, later) { now }.also { it.store = store }

    @Test fun secondLookupAnswersAtOnceAndRefreshesInTheBackground() {
        val resolver = Resolver(listOf(home))
        val later = Later()
        val store = Memory()
        val dns = dns(resolver, later, store)

        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertEquals(1, resolver.asked.size)
        assertEquals(listOf(home), store.saved["dash.local"])

        // The server moved. Within a minute nothing is re-checked; after it, the old
        // address is still returned without waiting, and the new one is used next time.
        resolver.answer = listOf(moved)
        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertTrue(later.queued.isEmpty())
        now += 61_000
        assertEquals(listOf(home), dns.lookup("Dash.Local"))
        assertEquals(1, resolver.asked.size)
        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertEquals("one background lookup at a time", 1, later.queued.size)
        later.run()
        assertEquals(listOf(moved), dns.lookup("dash.local"))
        assertEquals(listOf(moved), store.saved["dash.local"])
    }

    @Test fun aColdStartUsesTheAddressFromTheLastRun() {
        val resolver = Resolver(listOf(moved))
        val later = Later()
        val store = Memory().apply { saved["dash.local"] = listOf(home) }
        val dns = dns(resolver, later, store)

        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertTrue("answered without asking the network", resolver.asked.isEmpty())
        later.run()
        assertEquals(listOf(moved), dns.lookup("dash.local"))
    }

    @Test fun aFailedLookupKeepsTheAddressAndIsRetriedLater() {
        val resolver = Resolver(listOf(home))
        val later = Later()
        val store = Memory()
        val dns = dns(resolver, later, store)
        dns.lookup("dash.local")

        // mDNS drops an answer while the server is fine.
        resolver.answer = null
        now += 61_000
        assertEquals(listOf(home), dns.lookup("dash.local"))
        later.run()
        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertEquals(listOf(home), store.saved["dash.local"])
        assertTrue("no second lookup within a minute", later.queued.isEmpty())

        resolver.answer = listOf(moved)
        now += 61_000
        dns.lookup("dash.local")
        later.run()
        assertEquals(listOf(moved), dns.lookup("dash.local"))
    }

    @Test fun aFailedConnectionForgetsTheAddress() {
        val resolver = Resolver(listOf(home))
        val later = Later()
        val store = Memory()
        val dns = dns(resolver, later, store)
        dns.lookup("dash.local")

        // Away from home: nothing answers at the remembered address, and the name is unknown.
        resolver.answer = null
        dns.forget("Dash.Local")
        assertNull(store.saved["dash.local"])
        assertThrows(UnknownHostException::class.java) { dns.lookup("dash.local") }

        // Back home: it resolves and is remembered again.
        resolver.answer = listOf(home)
        assertEquals(listOf(home), dns.lookup("dash.local"))
        assertEquals(listOf(home), store.saved["dash.local"])
    }

    @Test fun onlyARealConnectionFailureForgets() {
        val store = Memory()
        val dns = dns(Resolver(listOf(home)), Later(), store)
        val listener = ForgetOnConnectFailure(dns)
        val client = OkHttpClient()
        fun fail(call: Call) = listener.connectFailed(call, InetSocketAddress(home, 80), Proxy.NO_PROXY, null, IOException())
        dns.lookup("dash.local")

        // A request the app cancelled says nothing about the address.
        fail(client.newCall(Request.Builder().url("http://dash.local/").build()).apply { cancel() })
        assertEquals(listOf(home), store.saved["dash.local"])

        fail(client.newCall(Request.Builder().url("http://dash.local/").build()))
        assertNull(store.saved["dash.local"])
    }

    @Test fun otherNamesAlwaysUseTheSystemResolver() {
        val resolver = Resolver(listOf(home))
        val later = Later()
        val store = Memory()
        val dns = dns(resolver, later, store)
        repeat(3) { dns.lookup("image.tmdb.org") }
        dns.lookup("192.168.0.30")
        dns.lookup("notlocal")
        assertEquals(5, resolver.asked.size)
        assertTrue(store.saved.isEmpty())
        assertTrue(later.queued.isEmpty())
    }
}
