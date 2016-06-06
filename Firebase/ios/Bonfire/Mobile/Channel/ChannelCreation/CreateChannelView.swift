import UIKit
import RxSwift
import RxCocoa

final class CreateChannelView: UIView {

    private let textField = UITextField()
    private let privacySwitch = UISwitch()
    private let privacyLabel = UILabel()
    private let submitButton = UIButton()

    weak var actionListener: CreateChannelActionListener?

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

    func setupViews() {
        backgroundColor = .whiteColor()

        textField.backgroundColor = .lightGrayColor()
        textField.clearButtonMode = .Always
        textField.placeholder = "Channel name here!"

        privacyLabel.text = "Private"

        submitButton.setTitleColor(.blackColor(), forState: .Normal)
        submitButton.setTitle("Create Channel", forState: .Normal)
    }

    func setupLayout() {
        addSubview(textField)
        addSubview(privacyLabel)
        addSubview(privacySwitch)
        addSubview(submitButton)

        textField.pinToSuperviewTop(withConstant: 20)
        textField.pinToSuperviewLeading(withConstant: 8)
        textField.pinToSuperviewTrailing(withConstant: 8)
        textField.addHeightConstraint(withConstant: 44)

        privacySwitch.attachToBottomOf(textField, withConstant: 20)
        privacySwitch.pinToSuperviewTrailing(withConstant: 8)

        privacySwitch.attachToRightOf(privacyLabel, withConstant: 8)
        privacyLabel.attachToBottomOf(textField, withConstant: 20)

        submitButton.attachToBottomOf(privacySwitch, withConstant: 20)
        submitButton.pinToSuperviewLeading(withConstant: 8)
        submitButton.pinToSuperviewTrailing(withConstant: 8)
        submitButton.addHeightConstraint(withConstant: 44)
    }

    func setupActions() {
        submitButton.rx_tap.subscribe(
            onNext: { [weak self] in
                self?.createChannel()
            }).addDisposableTo(disposeBag)
    }

    func createChannel() {
        guard let newChannelName = self.textField.text
            where !newChannelName.isEmpty
            else {
                return
        }
        self.actionListener?.createChannel(withName: newChannelName, privateChannel: self.privacySwitch.on)
    }

}

extension CreateChannelView: CreateChannelDisplayer {
    func displayError(error: ErrorType) {
        print("\(error)")
    }
}
