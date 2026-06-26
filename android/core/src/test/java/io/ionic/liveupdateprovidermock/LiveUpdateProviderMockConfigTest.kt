package io.ionic.liveupdateprovidermock

import io.ionic.liveupdateprovider.LiveUpdateProviderError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LiveUpdateProviderMockConfigTest {

    @Test
    fun `portals maps to PORTAL bundle type`() {
        val config = LiveUpdateProviderMockConfig.from(mapOf("bundleType" to "portals"))
        assertEquals(LiveUpdateProviderMockConfig.BundleType.PORTAL, config.bundleType)
    }

    @Test
    fun `federatedCapacitor maps to FEDERATED_CAPACITOR bundle type`() {
        val config = LiveUpdateProviderMockConfig.from(mapOf("bundleType" to "federatedCapacitor"))
        assertEquals(LiveUpdateProviderMockConfig.BundleType.FEDERATED_CAPACITOR, config.bundleType)
    }

    @Test
    fun `missing bundleType key throws InvalidConfiguration`() {
        assertThrows(LiveUpdateProviderError.InvalidConfiguration::class.java) {
            LiveUpdateProviderMockConfig.from(emptyMap())
        }
    }

    @Test
    fun `unknown bundleType value throws InvalidConfiguration`() {
        assertThrows(LiveUpdateProviderError.InvalidConfiguration::class.java) {
            LiveUpdateProviderMockConfig.from(mapOf("bundleType" to "invalid"))
        }
    }
}
