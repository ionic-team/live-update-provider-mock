package io.ionic.liveupdateprovidermock

import android.content.Context
import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import io.ionic.liveupdateprovider.ProviderManager
import io.ionic.liveupdateprovider.LiveUpdateProvider

@CapacitorPlugin(name = "LiveUpdateProviderMock")
class LiveUpdateProviderMockPlugin : Plugin(), LiveUpdateProvider {
    override fun createManager(context: Context, configuration: Map<String, Any>): ProviderManager {
        val providerConfig = MockProviderConfig.from(configuration)

        return MockProviderManager(
            context = context,
            config = providerConfig
        )
    }
}
