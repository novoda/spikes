import Foundation

class FirebaseDynamicLinkFactory: DynamicLinkFactory {

    let dynamicLinkDomain: String
    let bundleIdentifier: String
    let androidPackageName: String
    let deepLinkBaseURL: NSURL

    init(dynamicLinkDomain: String, bundleIdentifier: String, androidPackageName: String, deepLinkBaseURL: NSURL) {
        self.dynamicLinkDomain = dynamicLinkDomain
        self.bundleIdentifier = bundleIdentifier
        self.androidPackageName = androidPackageName
        self.deepLinkBaseURL = deepLinkBaseURL
    }

    func inviteLinkFromUser(user: User) -> NSURL {
        let deepLinkURL = welcomeDeepLinkFromUser(user)
        let deepLinkURLString = deepLinkURL.absoluteString

        let shareURL = NSURLComponents(string: dynamicLinkDomain)!
        shareURL.queryItems = [
            NSURLQueryItem(name: "link", value: deepLinkURLString),
            NSURLQueryItem(name: "ibi", value: bundleIdentifier),
            NSURLQueryItem(name: "apn", value: androidPackageName)
        ]

        return shareURL.URL!
    }

    private func welcomeDeepLinkFromUser(user: User) -> NSURL {
        let deeplinkURL = deepLinkBaseURL.URLByAppendingPathComponent("welcome")
        let deeplinkURLComponents = NSURLComponents(URL: deeplinkURL, resolvingAgainstBaseURL: true)!
        deeplinkURLComponents.queryItems = [
            NSURLQueryItem(name: "sender", value: user.identifier)
        ]
        return deeplinkURLComponents.URL!
    }

}
