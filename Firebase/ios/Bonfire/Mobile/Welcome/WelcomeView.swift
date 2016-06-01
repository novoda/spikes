import UIKit
import RxSwift
import RxCocoa

final class WelcomeView: UIView {

    private let welcomeMessageLabel = UILabel()
    private let doneButton = UIButton()
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
        welcomeMessageLabel.numberOfLines = 0
        if let sender = sender {
            welcomeMessageLabel.text = "Welcome to Bonfire. \(sender) invited you!"
        } else {
            welcomeMessageLabel.text = "Welcome to Bonfire"
        }

        doneButton.setTitle("Get Started", forState: .Normal)
        doneButton.setTitleColor(.blackColor(), forState: .Normal)
    }

    private func setupLayout() {
        addSubview(welcomeMessageLabel)
        addSubview(doneButton)

        welcomeMessageLabel.pinToSuperviewTop(withConstant: 20)
        welcomeMessageLabel.pinToSuperviewLeading(withConstant: 20)
        welcomeMessageLabel.pinToSuperviewTrailing(withConstant: 20)

        doneButton.attachToBottomOf(welcomeMessageLabel, withConstant: 10)
        doneButton.pinToSuperviewLeading(withConstant: 20)
        doneButton.pinToSuperviewTrailing(withConstant: 20)
        doneButton.addHeightConstraint(withConstant: 44)
    }

    private func setupActions() {
        doneButton.rx_tap.subscribe(
            onNext: { [weak self] in
                self?.welcomeDone()
            }).addDisposableTo(disposeBag)
    }

    private func welcomeDone() {
        actionListener?.welcomeDone()
    }
}