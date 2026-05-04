package io.ionic.liveupdateprovidermock

import io.ionic.liveupdateprovider.LiveUpdateProviderError

data class LiveUpdateConfig(
    val appType: AppType,
    val autoSync: Boolean
) {
    enum class AppType {
        FEDERATED_CAPACITOR,
        PORTALS;

        val assetBasePath: String
            get() = when (this) {
                FEDERATED_CAPACITOR -> "federated-capacitor"
                PORTALS -> "portals"
            }

        companion object {
            fun from(raw: String): AppType {
                return when (raw.lowercase()) {
                    "federatedcapacitor", "federated-capacitor", "fedcap" -> FEDERATED_CAPACITOR
                    "portals" -> PORTALS
                    else -> throw LiveUpdateProviderError.InvalidConfiguration("Invalid appType: $raw")
                }
            }
        }
    }

    companion object {
        fun from(config: Map<String, Any>): LiveUpdateConfig {
            val appTypeRaw = config["appType"] as? String
                ?: throw LiveUpdateProviderError.InvalidConfiguration("Missing required config key: appType")

            val autoSync = config["autoSync"] as? Boolean
                ?: throw LiveUpdateProviderError.InvalidConfiguration("Missing required config key: autoSync")

            return LiveUpdateConfig(
                appType = AppType.from(appTypeRaw),
                autoSync = autoSync
            )
        }
    }
}
