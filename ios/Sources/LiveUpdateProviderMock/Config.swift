import Foundation
import LiveUpdateProvider

public struct LiveUpdateConfig: Decodable {
    enum AppType: Decodable {
        case federatedCapacitor
        case portals
        
        init(_ raw: String) throws {
            switch raw.lowercased() {
            case "federatedcapacitor":
                self = .federatedCapacitor
            case "portals":
                self = .portals
            default:
                throw LiveUpdateProviderError.invalidConfiguration("Invalid appType: \(raw)", underlyingError: nil)
            }
        }
        
        var resourceBundleName: String {
            switch self {
            case .portals:
                return "LiveUpdateProviderMockResourcesPortals"
            case .federatedCapacitor:
                return "LiveUpdateProviderMockResourcesFedCap"
            }
        }
    }
    
    let appType: AppType
    let autoSync: Bool
    
    public init(_ config: [String: Any]) throws {
        guard let appTypeRaw = config["appType"] as? String else {
            throw LiveUpdateProviderError.invalidConfiguration("Missing required config key: appType", underlyingError: nil)
        }
        guard let autoSync = config["autoSync"] as? Bool else {
            throw LiveUpdateProviderError.invalidConfiguration("Missing required config key: autoSync", underlyingError: nil)
        }
        
        self.appType = try AppType(appTypeRaw)
        self.autoSync = autoSync
    }
}
