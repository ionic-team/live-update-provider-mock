import { registerPlugin } from '@capacitor/core';

export const LiveUpdateProviderMockPlugin = registerPlugin('LiveUpdateProviderMockPlugin');

export interface MockProviderConfig {
  appType: 'portals' | 'federatedCapacitor';
  autoSync: boolean;
}
