#import <Capacitor/Capacitor.h>

CAP_PLUGIN(MockLiveUpdateProviderPlugin, "MockLiveUpdateProvider",
           CAP_PLUGIN_METHOD(ping, CAPPluginReturnPromise);
           )
