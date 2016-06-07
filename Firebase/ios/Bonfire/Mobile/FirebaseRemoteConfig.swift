import Foundation
import Firebase
import FirebaseRemoteConfig

class FirebaseRemoteConfig: Config {

    let remoteConfig = FIRRemoteConfig.remoteConfig()

    init() {
        fetchConfig()
    }

    private func fetchConfig() {
        let remoteConfigSettings = FIRRemoteConfigSettings(developerModeEnabled: true)
        remoteConfig.configSettings = remoteConfigSettings!

        var expirationDuration: Double = 3600
        if remoteConfig.configSettings.isDeveloperModeEnabled {
            expirationDuration = 0
        }

        remoteConfig.fetchWithExpirationDuration(expirationDuration) { (status, error) in
            if status == .Success {
                self.remoteConfig.activateFetched()
            }
        }
    }

    // MARK: - Config Protocol

    func orderChannelsByName() -> Bool {
        let order = remoteConfig["orderChannelsByName"].boolValue
        return order
    }
}
