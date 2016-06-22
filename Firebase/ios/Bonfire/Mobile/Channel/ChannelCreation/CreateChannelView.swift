import UIKit

final class CreateChannelView: UIView {

    private let privacySwitch = UISwitch()
    private let privacyLabel = UILabel()
    private let privacyBackground = UIView()

    private let errorLabel = UILabel()

    private let channelNameLabel = UILabel()
    private let channelNameField = UITextField()
    private let channelNameFieldMask = UIView()
    private let channelNamePlaceholder = UILabel()

    private let submitButton = UIButton()

    private let scrollView = UIScrollView()
    private let contentView = UIView()

    weak var actionListener: CreateChannelActionListener?
    weak var viewController: UIViewController?

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
        backgroundColor = BonfireColors.lightGrey

        privacyBackground.backgroundColor = BonfireColors.mediumGrey

        privacyLabel.text = "Private Channel"

        privacySwitch.onTintColor = BonfireColors.orange

        channelNameLabel.text = "Channel Name"
        channelNameLabel.font = UIFont.systemFontOfSize(12)
        channelNameLabel.textColor = UIColor.darkGrayColor()

        channelNameField.backgroundColor = UIColor(white: 1.0, alpha: 0.78)
        channelNameField.borderStyle = .None
        channelNameField.clearButtonMode = .Always
        channelNameField.textAlignment = .Center
        channelNameField.font = UIFont.systemFontOfSize(45)
        channelNameField.accessibilityIdentifier = "NameField"

        channelNameField.delegate = self

        channelNamePlaceholder.text = "😘"
        channelNamePlaceholder.font = UIFont.systemFontOfSize(45)
        channelNamePlaceholder.textAlignment = .Center

        channelNameFieldMask.layer.cornerRadius = 50
        channelNameFieldMask.backgroundColor = .whiteColor()
        channelNameFieldMask.clipsToBounds = true

        errorLabel.textColor = .redColor()
        errorLabel.numberOfLines = 0
        errorLabel.font = UIFont.systemFontOfSize(12)
        errorLabel.text = ""
        errorLabel.textAlignment = .Center

        submitButton.setImage(UIImage(named: "ic_tick"), forState: .Normal)
        submitButton.backgroundColor = BonfireColors.greyHighlight
        submitButton.layer.cornerRadius = 25

    }

    func setupLayout() {
        addSubview(scrollView)
        scrollView.pinToSuperviewEdges()
        scrollView.addSubview(contentView)
        contentView.pinToSuperviewEdges()
        contentView.addEqualWidthConstraint(withView: scrollView)

        scrollView.alignHorizontalCenter(withView: contentView)


        contentView.addSubview(privacyBackground)
        contentView.addSubview(errorLabel)
        contentView.addSubview(channelNameLabel)
        contentView.addSubview(submitButton)
        contentView.addSubview(channelNameFieldMask)

        channelNameFieldMask.addSubview(channelNamePlaceholder)
        channelNameFieldMask.addSubview(channelNameField)

        privacyBackground.addSubview(privacyLabel)
        privacyBackground.addSubview(privacySwitch)

        privacyBackground.pinToSuperviewTop()
        privacyBackground.pinToSuperviewLeading()
        privacyBackground.pinToSuperviewTrailing()
        privacyBackground.addBottomBorder(withColor: BonfireColors.greyHighlight, height: 1)

        privacyLabel.pinToSuperviewTop(withConstant: 14)
        privacyLabel.pinToSuperviewBottom(withConstant: 14)
        privacyLabel.pinToSuperviewLeading(withConstant: 20)

        privacySwitch.pinToSuperviewTop(withConstant: 10)
        privacySwitch.pinToSuperviewTrailing(withConstant: 20)

        channelNameLabel.attachToBottomOf(privacyBackground, withConstant: 25)
        channelNameLabel.alignHorizontalCenterWithSuperview()

        channelNameFieldMask.attachToBottomOf(channelNameLabel, withConstant: 25)
        channelNameFieldMask.addHeightConstraint(withConstant: 100)
        channelNameFieldMask.addWidthConstraint(withConstant: 100)
        channelNameFieldMask.alignHorizontalCenterWithSuperview()

        channelNameField.pinToSuperviewEdges()
        channelNamePlaceholder.pinToSuperviewEdges()

        errorLabel.attachToBottomOf(channelNameFieldMask, withConstant: 15)
        errorLabel.pinToSuperviewLeading(withConstant: 30)
        errorLabel.pinToSuperviewTrailing(withConstant: 30)

        submitButton.attachToBottomOf(errorLabel, withConstant: 15)
        submitButton.alignHorizontalCenterWithSuperview()
        submitButton.addWidthConstraint(withConstant: 200)
        submitButton.addHeightConstraint(withConstant: 50)
        submitButton.pinToSuperviewBottom(withConstant: 10)
    }

    func setupActions() {
        submitButton.addTarget(self, action: #selector(createChannel), forControlEvents: .TouchUpInside)
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

extension CreateChannelView: UITextFieldDelegate {

    func textFieldShouldBeginEditing(textField: UITextField) -> Bool {
        channelNamePlaceholder.hidden = true
        return true
    }

}

extension CreateChannelView: CreateChannelDisplayer {
    func displayError(error: ErrorType) {
        errorLabel.text = "Sorry that channel name is either already in use, or is invalid. " +
        "Please try another name with a single emoji."
    }
}
