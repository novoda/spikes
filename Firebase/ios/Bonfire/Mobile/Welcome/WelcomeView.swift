import UIKit
import RxSwift
import RxCocoa

final class WelcomeView: UIView {

    private let senderProfileImage = UIImageView()
    private let senderLabel = UILabel()
    private let welcomeMessageLabel = UILabel()
    private let loginButton = UIButton()

    private var sender: String?

    weak var actionListener: WelcomeActionListener?

    private let disposeBag = DisposeBag()

    init(frame: CGRect, sender: String? = nil) {
        super.init(frame: frame)
        self.sender = sender
        setupViews()
        setupLayout()
        setupActions()
    }

    convenience init(sender: String? = nil) {
        self.init(frame: CGRect.zero, sender: sender)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        backgroundColor = .whiteColor()

        senderLabel.numberOfLines = 0
        senderLabel.textAlignment = .Center
        senderLabel.font = UIFont.systemFontOfSize(16)

        if let sender = sender {
            senderLabel.text = "\(sender) invited you to Bonfire "
        } else {
            senderLabel.text = "Welcome to Bonfire!"
        }

        welcomeMessageLabel.numberOfLines = 0
        welcomeMessageLabel.text = "Login and enjoy the emoji awesomeness"
        welcomeMessageLabel.textAlignment = .Center
        welcomeMessageLabel.font = UIFont.systemFontOfSize(16)

        loginButton.setTitle("Login", forState: .Normal)
        loginButton.setTitleColor(.whiteColor(), forState: .Normal)
        loginButton.layer.cornerRadius = 24
        loginButton.backgroundColor = BonfireColors.orange
    }

    private func setupLayout() {
        addSubview(senderProfileImage)
        addSubview(welcomeMessageLabel)
        addSubview(loginButton)
        addSubview(senderLabel)

        senderProfileImage.addHeightConstraint(withConstant: 86)
        senderProfileImage.addWidthConstraint(withConstant: 86)
        senderProfileImage.alignHorizontalCenterWithSuperview()

        senderLabel.attachToBottomOf(senderProfileImage, withConstant: 15)
        senderLabel.pinToSuperviewLeading(withConstant: 50)
        senderLabel.pinToSuperviewTrailing(withConstant: 50)

        welcomeMessageLabel.alignVerticalCenterWithSuperview()
        welcomeMessageLabel.attachToBottomOf(senderLabel, withConstant: 50)
        welcomeMessageLabel.pinToSuperviewLeading(withConstant: 20)
        welcomeMessageLabel.pinToSuperviewTrailing(withConstant: 20)

        loginButton.attachToBottomOf(welcomeMessageLabel, withConstant: 35)
        loginButton.pinToSuperviewLeading(withConstant: 75)
        loginButton.pinToSuperviewTrailing(withConstant: 75)
        loginButton.addHeightConstraint(withConstant: 48)
    }

    private func setupActions() {
        loginButton.rx_tap.subscribe(
            onNext: { [weak self] in
                self?.welcomeDone()
            }).addDisposableTo(disposeBag)
    }

    private func welcomeDone() {
        actionListener?.welcomeDone()
    }
}
