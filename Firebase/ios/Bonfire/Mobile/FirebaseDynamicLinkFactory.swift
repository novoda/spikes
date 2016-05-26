import Foundation

class FirebaseDynamicLinkFactory: DynamicLinkFactory {

    let dynamicLinkDomain: String
    let bundleIdentifier: String
    let deepLinkBaseURL: NSURL

    init(dynamicLinkDomain: String, bundleIdentifier: String, deepLinkBaseURL: NSURL) {
        self.dynamicLinkDomain = dynamicLinkDomain
        self.bundleIdentifier = bundleIdentifier
        self.deepLinkBaseURL = deepLinkBaseURL
    }

    func inviteLinkFromUser(user: User) -> NSURL {
        let deepLinkURL = welcomeDeepLinkFromUser(user)

        let shareURL = NSURLComponents(string: dynamicLinkDomain)!
        shareURL.queryItems = [
            NSURLQueryItem(name: "link", value: deepLinkURL.absoluteString),
            NSURLQueryItem(name: "ibi", value: bundleIdentifier)
        ]

        return shareURL.URL!
    }

    private func welcomeDeepLinkFromUser(user: User) -> NSURL {
        let deeplinkURL = deepLinkBaseURL.URLByAppendingPathComponent("welcome")
        let deeplinkURLComponents = NSURLComponents(URL: deeplinkURL, resolvingAgainstBaseURL: true)!
        deeplinkURLComponents.queryItems = [
            NSURLQueryItem(name: "sender", value: user.name)
        ]
        return deeplinkURLComponents.URL!
    }

}
