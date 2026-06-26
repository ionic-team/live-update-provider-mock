import Foundation
import LiveUpdateProvider

public struct LiveUpdateProviderMockConfig {
    enum BundleType {
        case federatedCapacitor
        case portal
        
        init(_ raw: String) throws {
            switch raw {
            case "federatedCapacitor":
                self = .federatedCapacitor
            case "portals":
                self = .portal
            default:
                throw LiveUpdateProviderError.invalidConfiguration("Invalid bundleType: \(raw)", underlyingError: nil)
            }
        }
        
        var resourceBundleName: String {
            switch self {
            case .portal:
                return "LiveUpdateProviderMockResourcesPortal"
            case .federatedCapacitor:
                return "LiveUpdateProviderMockResourcesFedCap"
            }
        }

        var spmResourceDirectoryName: String {
            switch self {
            case .portal:
                return "portal"
            case .federatedCapacitor:
                return "federated-capacitor"
            }
        }
    }
    
    let bundleType: BundleType
    
    public init(_ config: [String: Any]) throws {
        guard let bundleTypeRaw = config["bundleType"] as? String else {
            throw LiveUpdateProviderError.invalidConfiguration("Missing required config key: bundleType", underlyingError: nil)
        }
        
        self.bundleType = try BundleType(bundleTypeRaw)
    }
}
