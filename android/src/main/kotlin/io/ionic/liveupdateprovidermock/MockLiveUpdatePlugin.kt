package io.ionic.liveupdateprovidermock

import android.content.Context
import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import io.ionic.liveupdateprovider.LiveUpdateProvider
import io.ionic.liveupdateprovider.LiveUpdateProviderError
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProviderRegistry

@CapacitorPlugin(name = "MockLiveUpdateProvider")
class MockLiveUpdatePlugin : Plugin(), LiveUpdateProvider {
    override val id: String = "mock"

    override fun createManager(context: Context, config: Map<String, Any>): LiveUpdateProviderManager {
        val liveUpdateConfig = LiveUpdateConfig.from(config)

        val manager = MockLiveUpdateManager(
            context = context,
            config = liveUpdateConfig
        )

        if (liveUpdateConfig.autoSync && manager.latestAppDirectory == null) {
            throw LiveUpdateProviderError.InvalidConfiguration(
                "autoSync is enabled but bundled assets could not be resolved for appType='${liveUpdateConfig.appType}'"
            )
        }

        return manager
    }

    override fun load() {
        super.load()
        LiveUpdateProviderRegistry.register(this)
    }
}
