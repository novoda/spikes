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
        if let component = dynamicLink.url?.lastPathComponent where component == "welcome" {
            SharedServices.navigator.toWelcome()
        }
    }
}

struct SharedServices {
    static let loginService: LoginService = FirebaseLoginService()
    static let usersService: UsersService = FirebaseUsersService()
    static let channelsService: ChannelsService = FirebaseChannelsService()
    static let chatService: ChatService = FirebaseChatService()
    static let navigator: Navigator = AppNavigator()
    static let analytics: Analytics = FirebaseAnalytics()
    static let config: Config = FirebaseRemoteConfig()
}
