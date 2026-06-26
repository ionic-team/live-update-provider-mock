package io.ionic.liveupdateprovidermock

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class AssetBundleResolver(
    private val context: Context
) {
    fun resolve(bundleType: LiveUpdateProviderMockConfig.BundleType): File? {
        return runCatching {
            val assetPath = bundleType.assetBasePath

            if (!assetDirectoryExists(assetPath)) {
                null
            } else {
                val targetDir = File(context.cacheDir, "mock_bundles/$assetPath")
                targetDir.deleteRecursively()
                copyAssets(assetPath, targetDir)

                val entryFile = when (bundleType) {
                    LiveUpdateProviderMockConfig.BundleType.PORTAL -> "index.html"
                    LiveUpdateProviderMockConfig.BundleType.FEDERATED_CAPACITOR -> "remoteEntry.js"
                }
                targetDir.takeIf { File(it, entryFile).exists() }
            }
        }.getOrNull()
    }

    private fun assetDirectoryExists(assetPath: String): Boolean {
        return !context.assets.list(assetPath).isNullOrEmpty()
    }

    private fun copyAssets(assetPath: String, targetDir: File) {
        targetDir.mkdirs()
        context.assets.list(assetPath)?.forEach { name ->
            val childAsset = "$assetPath/$name"
            val childFile = File(targetDir, name)
            if (context.assets.list(childAsset).isNullOrEmpty()) {
                context.assets.open(childAsset).use { it.copyTo(FileOutputStream(childFile)) }
            } else {
                copyAssets(childAsset, childFile)
            }
        }
    }
}
