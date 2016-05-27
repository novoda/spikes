import UIKit
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import FirebaseAuth
import GoogleSignIn
import RxSwift

@UIApplicationMain
final class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    let disposeBag = DisposeBag()
    var userService: UsersService!

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        FIRApp.configure()
        GIDSignIn.sharedInstance().clientID = FIRApp.defaultApp()?.options.clientID

//        try! FIRAuth.auth()?.signOut()
//        GIDSignIn.sharedInstance().signOut()

        let navigationController = (SharedServices.navigator as! AppNavigator).navigationController
        navigationController.pushViewController(LoginViewController.withDependencies(), animated: false)

        window = UIWindow(frame: UIScreen.mainScreen().bounds)
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()

        return true
    }

    func application(app: UIApplication, openURL url: NSURL, options: [String : AnyObject]) -> Bool {
        if FIRDynamicLinks.dynamicLinks()!.shouldHandleDynamicLinkFromCustomSchemeURL(url) {
            if let dynamicLink = FIRDynamicLinks.dynamicLinks()?.dynamicLinkFromCustomSchemeURL(url) {
                handleDynamicLink(dynamicLink)
            }
            return true
        }

        if url.scheme.hasPrefix("com.googleusercontent.apps") {
            return GIDSignIn.sharedInstance().handleURL(url, sourceApplication: options[UIApplicationOpenURLOptionsSourceApplicationKey] as? String, annotation: options[UIApplicationOpenURLOptionsAnnotationKey])
        }

        return false
    }

    func application(application: UIApplication, continueUserActivity userActivity: NSUserActivity, restorationHandler: ([AnyObject]?) -> Void) -> Bool {
        let handled = FIRDynamicLinks.dynamicLinks()?.handleUniversalLink(userActivity.webpageURL!) { (dynamiclink, error) in
            if let dynamiclink = dynamiclink {
                self.handleDynamicLink(dynamiclink)
            }
        }

        
        return handled!
    }

    func handleDynamicLink(dynamicLink: FIRDynamicLink) {
        if let url = dynamicLink.url,
        let path = url.lastPathComponent where path == "welcome" {
            print(url)
            let components = NSURLComponents(URL: url, resolvingAgainstBaseURL: true)
            let sender = components?.queryItems?.first?.value
            print(sender)
            SharedServices.navigator.toWelcome(sender)
        }
    }
}

struct SharedServices {
    private static let firebase = FIRDatabase.database().reference()
    private static let channelsDatabase = FirebaseChannelsDatabase(
        publicChannelsDB: firebase.child("public-channels-index"),
        privateChannelsDB: firebase.child("private-channels-index"),
        channelsDB: firebase.child("channels"),
        ownersDB: firebase.child("owners")
    )

    private static let userDatabase = FirebaseUserDatabase(usersDB: firebase.child("users"))

    static let loginService: LoginService = FirebaseLoginService()
    static let usersService: UsersService = FirebaseUsersService()
    static let channelsService: ChannelsService = PersistedChannelsService(channelsDatabase: channelsDatabase, userDatabase: userDatabase)
    static let chatService: ChatService = FirebaseChatService()
    static let navigator: Navigator = AppNavigator()
    static let analytics: Analytics = FirebaseAnalytics()
    static let dynamicLinkFactory: DynamicLinkFactory = FirebaseDynamicLinkFactory(
        dynamicLinkDomain: "https://t6c2e.app.goo.gl",
        bundleIdentifier: "com.novoda.bonfire",
        deepLinkBaseURL: NSURL(string: "https://novoda.com/bonfire/welcome")!)
    static let config: Config = FirebaseRemoteConfig()
}
