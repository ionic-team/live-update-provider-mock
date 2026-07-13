package io.ionic.liveupdateprovidermock

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class AssetBundleResolver(
    private val context: Context
) {
    fun resolve(): File? {
        if (context.assets.list(ASSET_PATH).isNullOrEmpty()) {
            return null
        }

        val targetDir = File(context.noBackupFilesDir, ASSET_PATH)
        targetDir.deleteRecursively()
        copyAssets(ASSET_PATH, targetDir)

        return targetDir
    }

    private fun copyAssets(assetPath: String, targetDir: File) {
        targetDir.mkdirs()
        context.assets.list(assetPath)?.forEach { name ->
            val childAsset = "$assetPath/$name"
            val childFile = File(targetDir, name)
            if (context.assets.list(childAsset).isNullOrEmpty()) {
                context.assets.open(childAsset).use { input ->
                    FileOutputStream(childFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                copyAssets(childAsset, childFile)
            }
        }
    }

    companion object {
        private const val ASSET_PATH = "app"
    }
}