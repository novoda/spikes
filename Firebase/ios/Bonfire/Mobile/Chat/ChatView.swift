import UIKit
import RxSwift
import RxCocoa

final class ChatView: UIView {
    private let textField = UITextField()
    private let tableView = UITableView()
    private let tableViewManager = ChatTableViewManager()
    private weak var actionListener: ChatActionListener?

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

    private func setupViews() {
        tableViewManager.setupTableView(tableView)

        textField.backgroundColor = .lightGrayColor()
        textField.clearButtonMode = .Always
        textField.placeholder = "Type here!"
        textField.delegate = self
    }

    private func setupLayout() {
        addSubview(tableView)
        addSubview(textField)

        tableView.pinToSuperviewTop()
        textField.attachToBottomOf(tableView)
        textField.pinToSuperviewBottom()

        tableView.pinToSuperviewLeading()
        tableView.pinToSuperviewTrailing()

        textField.pinToSuperviewLeading()
        textField.pinToSuperviewTrailing()

        textField.addHeightConstraint(withConstant: 44)
    }
}

extension ChatView: ChatDisplayer {
    func display(chat: Chat) {
        tableViewManager.updateTableView(tableView, withChat: chat)
    }

    func attach(actionListener: ChatActionListener) {
        self.actionListener = actionListener
    }

    func detach(actionListener: ChatActionListener) {
        self.actionListener = nil
    }
}

extension ChatView: UITextFieldDelegate {
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        actionListener?.submitMessage(textField.text ?? "")
        textField.text = ""
        return true
    }
}
