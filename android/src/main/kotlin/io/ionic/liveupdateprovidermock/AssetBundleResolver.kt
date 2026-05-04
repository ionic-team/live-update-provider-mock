package io.ionic.liveupdateprovidermock

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class AssetBundleResolver(
    private val context: Context
) {
    fun resolve(appType: LiveUpdateConfig.AppType): File? {
        return runCatching {
            val assetPath = appType.assetBasePath

            if (!assetDirectoryExists(assetPath)) {
                null
            } else {
                val targetDir = File(context.cacheDir, "mock_bundles/$assetPath")
                targetDir.deleteRecursively()
                copyAssets(assetPath, targetDir)

                targetDir.takeIf { File(it, "index.html").exists() }
            }
        }.getOrNull()
    }

    private fun assetDirectoryExists(assetPath: String): Boolean {
        return !context.assets.list(assetPath).isNullOrEmpty()
    }

    private fun copyAssets(assetPath: String, targetDir: File) {
        data class CopyTask(val assetPath: String, val targetFile: File)

        val tasks = ArrayDeque<CopyTask>()
        tasks.add(CopyTask(assetPath, targetDir))

        while (tasks.isNotEmpty()) {
            val task = tasks.removeFirst()
            val children = context.assets.list(task.assetPath).orEmpty()

            if (children.isEmpty()) {
                task.targetFile.parentFile?.mkdirs()
                context.assets.open(task.assetPath).use { input ->
                    FileOutputStream(task.targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                task.targetFile.mkdirs()
                children.forEach { child ->
                    tasks.add(
                        CopyTask(
                            assetPath = "${task.assetPath}/$child",
                            targetFile = File(task.targetFile, child)
                        )
                    )
                }
            }
        }
    }
}
