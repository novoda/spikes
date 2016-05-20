import UIKit

final class CreateChannelView: UIView {

    private let textField = UITextField()
    weak var actionListener: CreateChannelActionListener?

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
        backgroundColor = .whiteColor()

        textField.backgroundColor = .lightGrayColor()
        textField.clearButtonMode = .Always
        textField.placeholder = "Channel name here!"
        textField.delegate = self
    }

    func setupLayout() {
        addSubview(textField)

        textField.pinToSuperviewTop(withConstant: 20)
        textField.pinToSuperviewLeading(withConstant: 8)
        textField.pinToSuperviewTrailing(withConstant: 8)
        textField.addHeightConstraint(withConstant: 44)
    }

}

extension CreateChannelView: CreateChannelDisplayer {
    func displayError(error: ErrorType) {
        print("\(error)")
    }
}

extension CreateChannelView: UITextFieldDelegate {
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        guard let newChannelName = textField.text
            where !newChannelName.isEmpty
            else {
                return false
            }
        actionListener?.createChannel(withName: newChannelName)
        textField.text = ""
        return true
    }
}
