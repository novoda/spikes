import UIKit
import Firebase
import GoogleSignIn

@UIApplicationMain
final class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var userService: UsersService!

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        FIRApp.configure()
        GIDSignIn.sharedInstance().clientID = FIRApp.defaultApp()?.options.clientID

        UIApplication.sharedApplication().statusBarStyle = .LightContent

        if let navigationController = (SharedServices.navigator as? AppNavigator)?.navigationController {
            navigationController.pushViewController(LoginViewController.withDependencies(), animated: false)
            window = UIWindow(frame: UIScreen.mainScreen().bounds)
            window?.rootViewController = navigationController
            window?.makeKeyAndVisible()
        }

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
            return GIDSignIn.sharedInstance()
                .handleURL(url,
                           sourceApplication: options[UIApplicationOpenURLOptionsSourceApplicationKey] as? String,
                           annotation: options[UIApplicationOpenURLOptionsAnnotationKey])
        }

        return false
    }

    func application(
        application: UIApplication,
        continueUserActivity userActivity: NSUserActivity,
                             restorationHandler: ([AnyObject]?) -> Void
        ) -> Bool {

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
