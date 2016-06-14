import UIKit
import GoogleSignIn

final class LoginView: UIView {

    let bubblesBackground = BubbleBackgroundView()

    private let googleButton = GIDSignInButton()
    weak var alertDelegate: AlertDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupLayout()
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setupViews() {
        backgroundColor = .orangeColor()
        googleButton.style = .Wide
    }

    func setupLayout() {
        addSubview(bubblesBackground)
        addSubview(googleButton)

        bubblesBackground.pinToSuperviewTop()
        bubblesBackground.pinToSuperviewLeading()
        bubblesBackground.pinToSuperviewTrailing()
        bubblesBackground.pinToSuperviewBottom()

        googleButton.pinToSuperviewTop(withConstant: 20)
        googleButton.pinToSuperviewLeading(withConstant: 16)
        googleButton.pinToSuperviewTrailing(withConstant: 16)
    }

}

extension LoginView: LoginDisplayer {
    func shouldShowAuthenticationError(message: String) {
        alertDelegate?.showAlert(title: nil, message: message)
    }
}
