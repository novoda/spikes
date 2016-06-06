import Foundation
import Firebase

class FirebaseAnalytics: Analytics {
    func viewChannel(channel: Channel) {
        FIRAnalytics.logEventWithName(kFIREventViewItem, parameters: [
            kFIRParameterItemID: channel.name,
            ])

        FIRAnalytics.logEventWithName("ViewChannel", parameters: [
            kFIRParameterItemID: channel.name,
            ])
    }
}
