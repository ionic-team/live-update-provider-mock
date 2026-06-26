import { registerPlugin } from '@capacitor/core';

export const LiveUpdateProviderMockPlugin = registerPlugin('LiveUpdateProviderMock');

export interface LiveUpdateProviderMockConfig {
  bundleType: 'portals' | 'federatedCapacitor';
}
