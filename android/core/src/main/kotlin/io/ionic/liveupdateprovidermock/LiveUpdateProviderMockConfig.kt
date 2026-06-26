package io.ionic.liveupdateprovidermock

import io.ionic.liveupdateprovider.LiveUpdateProviderError

data class LiveUpdateProviderMockConfig(
    val bundleType: BundleType
) {
    enum class BundleType {
        FEDERATED_CAPACITOR,
        PORTAL;

        val assetBasePath: String
            get() = when (this) {
                FEDERATED_CAPACITOR -> "federated-capacitor"
                PORTAL -> "portal"
            }

        companion object {
            fun from(raw: String): BundleType {
                return when (raw) {
                    "federatedCapacitor" -> FEDERATED_CAPACITOR
                    "portals" -> PORTAL
                    else -> throw LiveUpdateProviderError.InvalidConfiguration("Invalid bundleType: $raw")
                }
            }
        }
    }

    companion object {
        fun from(config: Map<String, Any>): LiveUpdateProviderMockConfig {
            val bundleTypeRaw = config["bundleType"] as? String
                ?: throw LiveUpdateProviderError.InvalidConfiguration("Missing required config key: bundleType")

            return LiveUpdateProviderMockConfig(
                bundleType = BundleType.from(bundleTypeRaw)
            )
        }
    }
}
