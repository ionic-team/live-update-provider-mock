package io.ionic.liveupdateprovidermock

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ionic.liveupdateprovider.LiveUpdateProviderError
import io.ionic.liveupdateprovider.LiveUpdateProviderSyncCallback
import io.ionic.liveupdateprovider.MetadataSyncResult
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LiveUpdateProviderMockManagerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPortalsSyncUsesPackagedResources() {
        val config = LiveUpdateProviderMockConfig.from(mapOf("bundleType" to "portals"))
        val manager = LiveUpdateProviderMockManager(context, config)
        val latch = CountDownLatch(1)

        manager.sync(object : LiveUpdateProviderSyncCallback {
            override fun onSuccess(result: MetadataSyncResult) = latch.countDown()
            override fun onFailure(error: LiveUpdateProviderError.SyncFailed) = latch.countDown()
        })

        assertTrue("Sync timed out", latch.await(5, TimeUnit.SECONDS))
        assertNotNull(manager.latestAppDirectory)
        assertTrue(File(manager.latestAppDirectory, "index.html").exists())
    }

    @Test
    fun testFederatedCapacitorSyncUsesPackagedResources() {
        val config = LiveUpdateProviderMockConfig.from(mapOf("bundleType" to "federatedCapacitor"))
        val manager = LiveUpdateProviderMockManager(context, config)
        val latch = CountDownLatch(1)

        manager.sync(object : LiveUpdateProviderSyncCallback {
            override fun onSuccess(result: MetadataSyncResult) = latch.countDown()
            override fun onFailure(error: LiveUpdateProviderError.SyncFailed) = latch.countDown()
        })

        assertTrue("Sync timed out", latch.await(5, TimeUnit.SECONDS))
        assertNotNull(manager.latestAppDirectory)
        assertTrue(File(manager.latestAppDirectory, "remoteEntry.js").exists())
    }
}
