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
    static let channelsService: ChannelsService = PersistedChannelsService(
        channelsDatabase:
        channelsDatabase,
        userDatabase: userDatabase
    )
    static let chatService: ChatService = PersistedChatService(chatDatabase: chatDatabase)
    static let navigator: Navigator = AppNavigator()
    static let analytics: Analytics = FirebaseAnalytics()
    static let dynamicLinkFactory: DynamicLinkFactory = FirebaseDynamicLinkFactory(
        dynamicLinkDomain: FirebaseIdentifiers.dynamicLinkDomain,
        bundleIdentifier: FirebaseIdentifiers.bundleIdentifier,
        androidPackageName: FirebaseIdentifiers.androidPackageName,
        deepLinkBaseURL: FirebaseIdentifiers.deepLinkBaseURL)
    static let config: Config = FirebaseRemoteConfig()
}
