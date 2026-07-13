import { registerPlugin } from '@capacitor/core';

export const LiveUpdateProviderMock = registerPlugin('LiveUpdateProviderMock');

export interface MockProviderConfig {
  /** Force `sync` to fail, to exercise error handling. Defaults to false. */
  simulateFailure?: boolean;
  /** Metadata returned with a successful sync result. Defaults to an empty object. */
  metadata?: Record<string, unknown>;
}
