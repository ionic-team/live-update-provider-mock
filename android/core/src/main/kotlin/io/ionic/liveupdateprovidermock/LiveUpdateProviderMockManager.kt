package io.ionic.liveupdateprovidermock

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.ionic.liveupdateprovider.DefaultMetadataSyncResult
import io.ionic.liveupdateprovider.MetadataSyncResult
import io.ionic.liveupdateprovider.LiveUpdateProviderError
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProviderSyncCallback
import java.io.File

class LiveUpdateProviderMockManager(
    private val context: Context,
    private val config: LiveUpdateProviderMockConfig
) : LiveUpdateProviderManager {

    companion object {
        private const val TAG = "LiveUpdateProviderMockManager"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val assetBundleResolver = AssetBundleResolver(context)

    override var latestAppDirectory: File? = null

    override fun sync(callback: LiveUpdateProviderSyncCallback?) {
        Thread {
            try {
                val appDirectory = assetBundleResolver.resolve(config.bundleType)
                latestAppDirectory = appDirectory
                Log.d(TAG, "Sync resolved directory: ${appDirectory?.absolutePath}")

                if (appDirectory == null) {
                    callback?.onFailureOnMain(
                        LiveUpdateProviderError.SyncFailed("bundled assets were not resolved")
                    )
                    return@Thread
                }

                callback?.onSuccessOnMain(syncResult())
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed", e)
                callback?.onFailureOnMain(
                    LiveUpdateProviderError.SyncFailed(e.message ?: "Unknown sync error", e)
                )
            }
        }.apply { isDaemon = true }.start()
    }

    private fun syncResult(): MetadataSyncResult {
        // Metadata is arbitrary fixture data. A production provider would return meaningful
        // values here, such as app version, checksum, or CDN URL.
        val metadata = when (config.bundleType) {
            LiveUpdateProviderMockConfig.BundleType.FEDERATED_CAPACITOR -> mapOf(
                "key1" to "value",
                "key2" to 0,
                "key3" to true
            )
            LiveUpdateProviderMockConfig.BundleType.PORTAL -> emptyMap()
        }

        return DefaultMetadataSyncResult(metadata = metadata)
    }

    private fun LiveUpdateProviderSyncCallback.onSuccessOnMain(result: MetadataSyncResult) {
        mainHandler.post { onSuccess(result) }
    }

    private fun LiveUpdateProviderSyncCallback.onFailureOnMain(error: LiveUpdateProviderError.SyncFailed) {
        mainHandler.post { onFailure(error) }
    }
}
