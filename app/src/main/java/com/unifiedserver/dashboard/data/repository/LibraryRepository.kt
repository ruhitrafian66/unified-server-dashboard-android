package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.*
import java.io.IOException
import javax.inject.Inject

open class LibraryRepository @Inject constructor(private val preferencesManager: PreferencesManager) {
    private suspend fun api() = buildApiService(preferencesManager)

    open suspend fun getLibrary(refresh: Boolean = false): Result<LibraryResponse> = apiResult {
        api().getLibrary(if (refresh) 1 else null).bodyOr { throw IOException("Server returned an empty library.") }
    }

    open suspend fun getItem(id: String): Result<LibraryItem> = apiResult {
        api().getLibraryItem(id).bodyOr { throw IOException("Server returned an empty item.") }
    }

    open suspend fun deleteItem(id: String): Result<LibraryDeleteResponse> = apiResult {
        api().deleteLibraryItem(id).bodyOr { LibraryDeleteResponse() }.also {
            if (!it.success) throw IOException("The server did not confirm the delete. Rescan before retrying.")
        }
    }

    open suspend fun cleanup(): Result<LibraryCleanupResponse> = apiResult {
        api().cleanupLibrary().bodyOr { LibraryCleanupResponse() }.also {
            if (!it.success) throw IOException("The server did not confirm the cleanup.")
        }
    }
}
