package io.ionic.liveupdateprovidermock

import android.content.Context
import android.util.Log
import io.ionic.liveupdateprovider.MetadataSyncResult
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.ProviderSyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private class MockSyncException(message: String) : Exception(message)

class MockProviderManager(
    private val context: Context,
    private val config: MockProviderConfig
) : ProviderManager {

    companion object {
        private const val TAG = "MockProviderManager"
    }

    private val assetBundleResolver = AssetBundleResolver(context)

    override var latestAppDirectory: File? = null

    override suspend fun sync(): ProviderSyncResult? = withContext(Dispatchers.IO) {
        if (config.simulateFailure) {
            throw MockSyncException("Simulated sync failure for LiveUpdateProviderMock")
        }

        val appDirectory = assetBundleResolver.resolve()
            ?: throw MockSyncException("Can't find resource directory")
        latestAppDirectory = appDirectory
        Log.d(TAG, "Sync resolved directory: ${appDirectory.absolutePath}")

        MetadataSyncResult(metadata = config.metadata)
    }
}
