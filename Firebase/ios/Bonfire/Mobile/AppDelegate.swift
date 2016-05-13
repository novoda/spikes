import UIKit
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import FirebaseAuth
import GoogleSignIn

@UIApplicationMain
final class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

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
        if url.scheme.hasPrefix("com.googleusercontent.apps") {
            return GIDSignIn.sharedInstance().handleURL(url, sourceApplication: options[UIApplicationOpenURLOptionsSourceApplicationKey] as? String, annotation: options[UIApplicationOpenURLOptionsAnnotationKey])
        }
        return false
    }
}

struct SharedServices {
    static let loginService: LoginService = FirebaseLoginService()
    static let chatService: ChatService = FirebaseChatService()
    static let navigator: Navigator = AppNavigator()
}
