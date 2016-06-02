import Foundation
import Firebase

struct SharedServices {
    private static let firebase = FIRDatabase.database().reference()
    private static let channelsDatabase = FirebaseChannelsDatabase(
        publicChannelsDB: firebase.child("public-channels-index"),
        privateChannelsDB: firebase.child("private-channels-index"),
        channelsDB: firebase.child("channels"),
        ownersDB: firebase.child("owners")
    )
    private static let userDatabase = FirebaseUserDatabase(usersDB: firebase.child("users"))
    private static let chatDatabase = FirebaseChatDatabase(messagesDB: firebase.child("messages"))

    static let loginService: LoginService = FirebaseLoginService()
    static let usersService: UsersService = PersistedUserService(userDatabase: userDatabase)
    static let channelsService: ChannelsService = PersistedChannelsService(channelsDatabase: channelsDatabase, userDatabase: userDatabase)
    static let chatService: ChatService = PersistedChatService(chatDatabase: chatDatabase)
    static let navigator: Navigator = AppNavigator()
    static let analytics: Analytics = FirebaseAnalytics()
    static let dynamicLinkFactory: DynamicLinkFactory = FirebaseDynamicLinkFactory(
        dynamicLinkDomain: "https://t6c2e.app.goo.gl",
        bundleIdentifier: "com.novoda.bonfire",
        androidPackageName: "com.novoda.bonfire",
        deepLinkBaseURL: NSURL(string: "https://novoda.com/bonfire")!)
    static let config: Config = FirebaseRemoteConfig()
}
