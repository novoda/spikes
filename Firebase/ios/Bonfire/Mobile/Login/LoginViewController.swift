import UIKit
import GoogleSignIn

final class LoginViewController: UIViewController {

    let loginView = LoginView()
    let loginPresenter: LoginPresenter

    static func withDependencies() -> LoginViewController {
        return LoginViewController(loginService: SharedServices.loginService, navigator: SharedServices.navigator)
    }

    init(loginService: LoginService, navigator: Navigator) {
        self.loginPresenter = LoginPresenter(loginService: loginService, loginDisplayer: loginView, navigator: navigator)
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        self.view = UIView()

        UIApplication.sharedApplication().statusBarStyle = .Default
        navigationController?.navigationBarHidden = true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
        title = "Login"
        loginView.alertDelegate = self
        GIDSignIn.sharedInstance().delegate = self
        GIDSignIn.sharedInstance().uiDelegate = self
    }

    private func setupLayout() {
        view.addSubview(loginView)
        loginView.pinToTopLayoutGuide(viewController: self)
        loginView.pinToSuperviewBottom()
        loginView.pinToSuperviewLeading()
        loginView.pinToSuperviewTrailing()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        loginPresenter.startPresenting()
    }

    override func viewDidDisappear(animated: Bool) {
        loginPresenter.stopPresenting()
        super.viewDidDisappear(animated)
    }
}

extension LoginViewController: GIDSignInUIDelegate {}

extension LoginViewController: GIDSignInDelegate {
    func signIn(signIn: GIDSignIn!, didSignInForUser user: GIDGoogleUser!, withError error: NSError!) {
        if let error = error {
            loginPresenter.googleLoginFailed(error.localizedDescription)
            return
        }

        let authentication = user.authentication
        loginPresenter.googleLoginSuccess(idToken: authentication.idToken, accessToken: authentication.accessToken)
    }

    func signIn(signIn: GIDSignIn!, didDisconnectWithUser user: GIDGoogleUser!, withError error: NSError!) {

    }
}
