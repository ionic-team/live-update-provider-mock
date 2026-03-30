package io.ionic.liveupdateprovider.mock

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.ProviderSyncCallback
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.edit

/**
 * Mock implementation of LiveUpdateProviderManager.
 *
 * Copies bundled assets to cache directory and manages sync state via SharedPreferences.
 * Mimics iOS behavior but adapted for Android patterns (callbacks vs async/await).
 */
class MockLiveUpdateManager(
    private val context: Context,
    private val appType: String,
    private val managerKey: String,
    private val syncTo: String,
    private val persistSync: Boolean
) : LiveUpdateProviderManager {

    companion object {
        private const val TAG = "MockLiveUpdateManager"
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    private val sharedPrefs = context.getSharedPreferences("MockLiveUpdateProvider", Context.MODE_PRIVATE)
    private val mainHandler = Handler(Looper.getMainLooper())

    override var latestAppDirectory: File? = null

    init {
        // Restore persisted sync state if enabled
        if (persistSync) {
            val persistedSyncTo = sharedPrefs.getString(managerKey, null)
            if (persistedSyncTo != null) {
                latestAppDirectory = resolveDirectory(persistedSyncTo)
                if (latestAppDirectory == null) {
                    // Fallback to initial syncTo if persisted path doesn't exist
                    latestAppDirectory = resolveDirectory(syncTo)
                }
            }
        }
    }

    override fun sync(callback: ProviderSyncCallback?) {
        mainHandler.post {
            try {
                latestAppDirectory = resolveDirectory(syncTo)
                Log.d("MockLiveUpdateManager", "Sync resolved directory: ${latestAppDirectory?.absolutePath}")

                if (persistSync) {
                    sharedPrefs.edit { putString(managerKey, syncTo) }
                }

                val result = io.ionic.liveupdateprovider.FederatedCapacitorSyncResult(
                    didUpdate = true,
                    metadata = emptyMap()
                )
                callback?.onSuccess(result)
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed", e)
                val error = io.ionic.liveupdateprovider.LiveUpdateError.SyncFailed(
                    details = e.message ?: "Unknown sync error",
                    cause = e
                )
                callback?.onFailure(error)
            }
        }
    }

    /**
     * Resolves the directory for the given syncTo path.
     *
     * Strategy:
     * 1. Determine asset base path (portals/ or fedcap/)
     * 2. Copy assets from APK to cache directory (required for File access)
     * 3. Verify index.html exists
     * 4. Return cache directory File or null
     */
    private fun resolveDirectory(syncTo: String): File? {
        val assetBasePath = if (appType == "portals") "portals" else "fedcap"
        val fullAssetPath = "$assetBasePath/$syncTo"

        // Check if assets exist
        try {
            val assetList = context.assets.list(fullAssetPath)
            if (assetList.isNullOrEmpty()) {
                Log.w(TAG, "No assets found at $fullAssetPath")
                return null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Asset path does not exist: $fullAssetPath", e)
            return null
        }

        // Cache directory for extracted assets
        val cacheDir = File(context.cacheDir, "mock_bundles/$appType/$syncTo")

        // Check if cache is fresh (avoid repeated extraction)
        if (cacheDir.exists() && isCacheFresh(cacheDir)) {
            val indexFile = File(cacheDir, "index.html")
            if (indexFile.exists()) {
                Log.d(TAG, "Using cached assets at ${cacheDir.absolutePath}")
                return cacheDir
            }
        }

        // Copy assets to cache
        return try {
            copyAssetsToCache(fullAssetPath, cacheDir)
            val indexFile = File(cacheDir, "index.html")
            if (indexFile.exists()) {
                Log.d(TAG, "Extracted assets to ${cacheDir.absolutePath}")
                cacheDir
            } else {
                Log.w(TAG, "index.html not found after extraction")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy assets", e)
            null
        }
    }

    /**
     * Iteratively copies assets from APK to cache directory.
     */
    private fun copyAssetsToCache(assetPath: String, targetDir: File) {
        data class CopyTask(val assetPath: String, val targetFile: File)

        val tasks = mutableListOf(CopyTask(assetPath, targetDir))

        while (tasks.isNotEmpty()) {
            val task = tasks.removeAt(0)

            val assetList = context.assets.list(task.assetPath) ?: emptyArray()
            if (assetList.isEmpty()) {
                // It's a file, not a directory
                task.targetFile.parentFile?.mkdirs()
                context.assets.open(task.assetPath).use { input ->
                    FileOutputStream(task.targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                // It's a directory, queue up children
                task.targetFile.mkdirs()
                for (asset in assetList) {
                    val childAssetPath = "${task.assetPath}/$asset"
                    val childTargetFile = File(task.targetFile, asset)
                    tasks.add(CopyTask(childAssetPath, childTargetFile))
                }
            }
        }
    }

    /**
     * Checks if cache is fresh (modified within CACHE_EXPIRY_MS).
     */
    private fun isCacheFresh(cacheDir: File): Boolean {
        if (!cacheDir.exists()) return false
        val now = System.currentTimeMillis()
        val lastModified = cacheDir.lastModified()
        return (now - lastModified) < CACHE_EXPIRY_MS
    }
}
