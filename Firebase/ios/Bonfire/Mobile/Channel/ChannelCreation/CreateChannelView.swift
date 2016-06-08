import UIKit
import RxSwift
import RxCocoa

final class CreateChannelView: UIView {

    private let errorLabel = UILabel()
    private let channelNameLabel = UILabel()
    private let channelNameField = UITextField()
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

        channelNameLabel.text = "Channel emoji name"

        channelNameField.backgroundColor = .clearColor()
        channelNameField.borderStyle = .RoundedRect
        channelNameField.clearButtonMode = .Always
        channelNameField.placeholder = ":-)"
        channelNameField.accessibilityIdentifier = "NameField"

        errorLabel.textColor = .redColor()
        errorLabel.numberOfLines = 0
        errorLabel.font = UIFont.systemFontOfSize(12)
        errorLabel.text = ""

        privacyLabel.text = "Private"

        submitButton.setTitleColor(.blueColor(), forState: .Normal)
        submitButton.setTitle("Create Channel", forState: .Normal)
    }

    func setupLayout() {
        addSubview(errorLabel)
        addSubview(channelNameLabel)
        addSubview(channelNameField)
        addSubview(privacyLabel)
        addSubview(privacySwitch)
        addSubview(submitButton)

        errorLabel.pinToSuperviewTop(withConstant: 10)
        errorLabel.pinToSuperviewLeading(withConstant: 16)
        errorLabel.pinToSuperviewTrailing(withConstant: 16)

        channelNameLabel.attachToBottomOf(errorLabel, withConstant: 10)
        channelNameLabel.pinToSuperviewLeading(withConstant: 16)
        channelNameLabel.addHeightConstraint(withConstant: 44)

        channelNameField.attachToRightOf(channelNameLabel)
        channelNameField.attachToBottomOf(errorLabel, withConstant: 16)
        channelNameField.pinToSuperviewTrailing(withConstant: 16)
        channelNameField.addWidthConstraint(withConstant: 100)

        privacySwitch.attachToBottomOf(channelNameField, withConstant: 20)
        privacySwitch.pinToSuperviewTrailing(withConstant: 16)
        privacySwitch.attachToRightOf(privacyLabel, withConstant: 16)

        privacyLabel.attachToBottomOf(channelNameField, withConstant: 24)

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
        guard let newChannelName = self.channelNameField.text
            where !newChannelName.isEmpty
            else {
                return
        }
        self.actionListener?.createChannel(withName: newChannelName, privateChannel: self.privacySwitch.on)
    }

}

extension CreateChannelView: CreateChannelDisplayer {
    func displayError(error: ErrorType) {
        errorLabel.text = "Sorry that channel name is either already in use, or is invalid. Please try another name with a single emoji."
    }
}
