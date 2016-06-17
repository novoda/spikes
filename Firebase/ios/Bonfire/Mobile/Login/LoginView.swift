import UIKit
import GoogleSignIn


final class LoginView: UIView {

    let bubblesBackground = BubbleBackgroundView()
    let logoView = LogoView()
    let disclaimerLabel = UILabel()

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
        backgroundColor = BonfireColors.orange

        disclaimerLabel.text = "This application is for demo purposes only. Signing in via your Google account will give us access to your name and profile image only, which will be publicly available to all other users of the application. The messages are not encrypted and any Novoda employee can potentially see them. The database, including all channels and messages can be erased at anytime."
        disclaimerLabel.textAlignment = .Center
        disclaimerLabel.numberOfLines = 0
        disclaimerLabel.font = UIFont.systemFontOfSize(12)
        disclaimerLabel.textColor = .whiteColor()

        googleButton.style = .Wide

        bubblesBackground.updateWithView(logoView)
    }

    func setupLayout() {
        addSubview(bubblesBackground)
        addSubview(disclaimerLabel)
        addSubview(googleButton)

        bubblesBackground.pinToSuperviewTop()
        bubblesBackground.pinToSuperviewLeading()
        bubblesBackground.pinToSuperviewTrailing()
        bubblesBackground.pinToSuperviewBottom()

        disclaimerLabel.pinToSuperviewLeading(withConstant: 25)
        disclaimerLabel.pinToSuperviewTrailing(withConstant: 25)

        googleButton.attachToBottomOf(disclaimerLabel, withConstant: 40)
        googleButton.pinToSuperviewLeading(withConstant: 16)
        googleButton.pinToSuperviewTrailing(withConstant: 16)
        googleButton.pinToSuperviewBottom(withConstant: 50)
    }

}

extension LoginView: LoginDisplayer {
    func shouldShowAuthenticationError(message: String) {
        alertDelegate?.showAlert(title: nil, message: message)
    }
}
