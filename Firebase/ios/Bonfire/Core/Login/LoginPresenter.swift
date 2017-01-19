import RxSwift

class LoginPresenter {
    let loginService: LoginService
    let loginDisplayer: LoginDisplayer
    let navigator: Navigator

    var disposeBag: DisposeBag!

    init(loginService: LoginService, loginDisplayer: LoginDisplayer, navigator: Navigator) {
        self.loginService = loginService
        self.loginDisplayer = loginDisplayer
        self.navigator = navigator
    }

    func startPresenting() {
        disposeBag = DisposeBag()

        loginService.user().subscribe(
            onNext: { [weak self] auth in
                self?.handleAuth(auth)
        }).addDisposableTo(disposeBag)
    }

    func stopPresenting() {
        disposeBag = nil
    }

    private func handleAuth(auth: Authentication) {
        if auth.isSuccess() {
            navigator.toChat()
        } else {
            let error = auth.failure as! NSError
            loginDisplayer.shouldShowAuthenticationError(error.localizedDescription)
        }
    }
}

extension LoginPresenter {
    func googleLoginSuccess(idToken idToken: String, accessToken: String) {
        loginService.loginWithGoogle(idToken: idToken, accessToken: accessToken)
    }

    func googleLoginFailed(message: String) {
        loginDisplayer.shouldShowAuthenticationError(message)
    }
}
