import UIKit
import RxSwift
import RxCocoa

protocol ChatViewNavigationItemDelegate: class {
    func updateNavigationItem(withChat chat: Chat)
}

final class ChatView: UIView {
    private let textField = UITextField()
    private let tableView = UITableView()
    private let tableViewManager = ChatTableViewManager()
    weak var actionListener: ChatActionListener?
    weak var navigationItemDelegate: ChatViewNavigationItemDelegate?

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
        navigationItemDelegate?.updateNavigationItem(withChat: chat)
    }
}

extension ChatView {
    func updateNavigationItem(navigationItem: UINavigationItem, chat: Chat) {
        navigationItem.title = chat.channel.name

        if chat.channel.access == .Private {
            let barButtonItem = UIBarButtonItem(
                barButtonSystemItem: .Add,
                target: self,
                action: #selector(addUsers)
            )
            navigationItem.rightBarButtonItem = barButtonItem
        } else {
            navigationItem.rightBarButtonItem = nil
        }
    }

    func addUsers() {
        actionListener?.addUsers()
    }
}

extension ChatView: UITextFieldDelegate {
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        actionListener?.submitMessage(textField.text ?? "")
        textField.text = ""
        return true
    }
}
