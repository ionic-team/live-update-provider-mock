package io.ionic.liveupdateprovider.mock

import android.content.Context
import android.util.Log
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import io.ionic.liveupdateprovider.LiveUpdateProviderManager
import io.ionic.liveupdateprovider.LiveUpdateError
import io.ionic.liveupdateprovider.LiveUpdateProvider
import io.ionic.liveupdateprovider.LiveUpdateProviderRegistry

/**
 * Mock Live Updates Provider for testing and demonstration purposes.
 *
 * This provider simulates live update behavior without making actual network requests,
 * allowing developers to test the provider integration pattern and configuration parsing.
 *
 * ## Features:
 * - Resolves bundled local assets without network calls
 * - Supports both Portals and Federated Capacitor app types
 * - Available before FedCapPlugin might need it (addressing plugin load order issues)
 *
 * ## Configuration:
 * ```json
 * {
 *   "liveUpdateConfig": {
 *     "providerId": "mock",
 *     "providerConfig": {
 *       "managerKey": "app-id",
 *       "syncTo": "aurora",
 *       "persistSync": true,
 *       "appType": "fedcap"
 *     }
 *   }
 * }
 * ```
 */
@CapacitorPlugin(name = "MockLiveUpdateProvider")
class MockLiveUpdatePlugin : Plugin(), LiveUpdateProvider {


    override val id: String = "mock"

    @Throws(LiveUpdateError.InvalidConfiguration::class)
    override fun createManager(context: Context, config: Map<String, Any>?): LiveUpdateProviderManager {
        if (config == null) {
            throw LiveUpdateError.InvalidConfiguration("Missing provider config")
        }

        val managerKey = config["managerKey"] as? String
            ?: throw LiveUpdateError.InvalidConfiguration("Missing required config key: managerKey")

        val syncTo = config["syncTo"] as? String
            ?: throw LiveUpdateError.InvalidConfiguration("Missing required config key: syncTo")

        val persistSync = config["persistSync"] as? Boolean
            ?: throw LiveUpdateError.InvalidConfiguration("Missing required config key: persistSync")

        val appType = config["appType"] as? String ?: "fedcap"

        return MockLiveUpdateManager(
            context = context,
            appType = appType,
            managerKey = managerKey,
            syncTo = syncTo,
            persistSync = persistSync
        )
    }

    @PluginMethod
    fun ping(call: PluginCall) {
        val ret = com.getcapacitor.JSObject()
        ret.put("ok", true)
        call.resolve(ret)
    }

    private val TAG = "LiveUpdateProviderPlugin"

    /**
     * Helper method to register a provider plugin.
     * Call this from your provider's companion init block.
     *
     * This method is thread-safe and idempotent for each provider ID.
     */
    private fun registerProviderPlugin(
        provider: LiveUpdateProvider,
    ) {
        try {
            // Check if already registered to avoid duplicate registration
            val existing = LiveUpdateProviderRegistry.resolve(provider.id)
            if (existing != null) {
                Log.d(TAG, "Provider '${provider.id}' already registered, skipping")
                return
            }

            LiveUpdateProviderRegistry.register(provider)
            Log.d(TAG, "Successfully registered provider: ${provider.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register provider: ${e.message}", e)
        }
    }

    /**
     * Plugin lifecycle method called by Capacitor after plugin is loaded.
     *
     * Subclasses can override this method to perform additional initialization,
     * but must call super.load() to maintain proper lifecycle.
     */
    override fun load() {
        super.load()
        Log.d(TAG, "Provider plugin loaded: $id")
        registerProviderPlugin(this)
    }
}
