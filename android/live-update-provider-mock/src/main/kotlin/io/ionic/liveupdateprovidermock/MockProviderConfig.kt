package io.ionic.liveupdateprovidermock

import io.ionic.liveupdateprovider.ProviderError

data class MockProviderConfig(
    /** When true, [MockProviderManager.sync] fails so callers can exercise error handling. */
    val simulateFailure: Boolean,
    /** Metadata returned with a successful sync result. */
    val metadata: Map<String, Any>
) {
    companion object {
        fun from(config: Map<String, Any>): MockProviderConfig {
            val simulateFailure = when (val raw = config["simulateFailure"]) {
                null -> false
                is Boolean -> raw
                else -> throw ProviderError.InvalidConfiguration("simulateFailure must be a boolean")
            }

            @Suppress("UNCHECKED_CAST")
            val metadata = when (val raw = config["metadata"]) {
                null -> emptyMap()
                is Map<*, *> -> raw as Map<String, Any>
                else -> throw ProviderError.InvalidConfiguration("metadata must be an object")
            }

            return MockProviderConfig(
                simulateFailure = simulateFailure,
                metadata = metadata
            )
        }
    }
}
