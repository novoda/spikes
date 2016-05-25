import UIKit
import RxSwift
import RxCocoa

final class WelcomeView: UIView {

    private let welcomeMessageLabel = UILabel()
    private let doneButton = UIButton()

    weak var actionListener: WelcomeActionListener?

    private let disposeBag = DisposeBag()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupLayout()
        setupActions()
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        backgroundColor = .whiteColor()
        welcomeMessageLabel.text = "Welcome to Bonfire"

        doneButton.setTitle("Get Started", forState: .Normal)
        doneButton.setTitleColor(.blackColor(), forState: .Normal)
    }

    private func setupLayout() {
        addSubview(welcomeMessageLabel)
        addSubview(doneButton)

        welcomeMessageLabel.pinToSuperviewTop(withConstant: 20)
        welcomeMessageLabel.pinToSuperviewLeading(withConstant: 20)

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