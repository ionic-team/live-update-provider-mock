import { registerPlugin } from '@capacitor/core';

export interface MockLiveUpdateProviderPlugin {
  ping(): Promise<{ ok: boolean }>;
}

export const MockLiveUpdateProvider = registerPlugin<MockLiveUpdateProviderPlugin>('MockLiveUpdateProvider');

export interface MockProviderConfig {
  appType: 'fedcap';
  managerKey: string;
  syncTo: string;
  persistSync: boolean;
}
