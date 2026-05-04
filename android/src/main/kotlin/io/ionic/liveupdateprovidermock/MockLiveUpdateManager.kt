package io.ionic.liveupdateprovidermock

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.ionic.liveupdateprovider.FederatedCapacitorSyncResult
import io.ionic.liveupdateprovider.LiveUpdateProviderError
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProviderSyncCallback
import java.io.File
import java.util.concurrent.Executors

class MockLiveUpdateManager(
    private val context: Context,
    private val config: LiveUpdateConfig
) : LiveUpdateProviderManager {

    companion object {
        private const val TAG = "MockLiveUpdateManager"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val syncExecutor = Executors.newSingleThreadExecutor()
    private val assetBundleResolver = AssetBundleResolver(context)

    override var latestAppDirectory: File? = null

    init {
        if (config.autoSync) {
            latestAppDirectory = assetBundleResolver.resolve(config.appType)
        }
    }

    override fun sync(callback: LiveUpdateProviderSyncCallback?) {
        syncExecutor.execute {
            try {
                val appDirectory = assetBundleResolver.resolve(config.appType)
                latestAppDirectory = appDirectory
                Log.d(TAG, "Sync resolved directory: ${appDirectory?.absolutePath}")

                if (appDirectory == null) {
                    callback?.onFailureOnMain(
                        LiveUpdateProviderError.SyncFailed("bundled assets were not resolved")
                    )
                    return@execute
                }

                callback?.onSuccessOnMain(syncResult())
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed", e)
                callback?.onFailureOnMain(
                    LiveUpdateProviderError.SyncFailed(e.message ?: "Unknown sync error", e)
                )
            }
        }
    }

    private fun syncResult(): FederatedCapacitorSyncResult {
        val metadata = when (config.appType) {
            LiveUpdateConfig.AppType.FEDERATED_CAPACITOR -> mapOf(
                "key1" to "value",
                "key2" to 0,
                "key3" to true
            )
            LiveUpdateConfig.AppType.PORTALS -> emptyMap()
        }

        return FederatedCapacitorSyncResult(metadata = metadata)
    }

    private fun LiveUpdateProviderSyncCallback.onSuccessOnMain(result: FederatedCapacitorSyncResult) {
        mainHandler.post { onSuccess(result) }
    }

    private fun LiveUpdateProviderSyncCallback.onFailureOnMain(error: LiveUpdateProviderError.SyncFailed) {
        mainHandler.post { onFailure(error) }
    }
}
