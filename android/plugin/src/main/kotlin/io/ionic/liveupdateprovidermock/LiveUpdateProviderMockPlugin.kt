package io.ionic.liveupdateprovidermock

import android.content.Context
import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import io.ionic.liveupdateprovider.LiveUpdateProvider
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProviderRegistry

@CapacitorPlugin(name = "LiveUpdateProviderMock")
class LiveUpdateProviderMockPlugin : Plugin(), LiveUpdateProvider {
    override val id: String = "mock"

    override fun createManager(context: Context, config: Map<String, Any>): LiveUpdateProviderManager {
        val providerConfig = LiveUpdateProviderMockConfig.from(config)

        return LiveUpdateProviderMockManager(
            context = context,
            config = providerConfig
        )
    }

    override fun load() {
        super.load()
        LiveUpdateProviderRegistry.register(this)
    }
}
