import Foundation
import RxSwift

final class LoginPresenter {
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
            navigator.toChannels()
        } else {
            guard let error = auth.failure as? NSError else { return }
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
