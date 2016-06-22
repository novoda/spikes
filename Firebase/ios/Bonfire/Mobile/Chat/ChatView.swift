import UIKit

protocol ChatViewNavigationItemDelegate: class {
    func updateNavigationItem(withChat chat: Chat)
}

final class ChatView: UIView {
    private let tableViewManager = ChatTableViewManager()
    private let tableView = UITableView()

    private let textField = UITextField()
    private let sendButton = UIButton(type: .System)
    private let textFieldBackgroundView = UIView()
    private let messageEntryView = UIView()

    private let horizontalSpacing: CGFloat = 8
    private let verticalSpacing: CGFloat = 12
    private let textEntryHeight: CGFloat = 48

    weak var actionListener: ChatActionListener?
    weak var navigationItemDelegate: ChatViewNavigationItemDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupHierarchy()
        setupLayout()
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        tintColor = UIColor(RGBred: 248, green: 86, blue: 54)
        tableViewManager.setupTableView(tableView)

        textField.backgroundColor = .clearColor()
        textField.clearButtonMode = .Always
        textField.placeholder = "Message"
        textField.delegate = self
        textField.font = UIFont.systemFontOfSize(14)

        textFieldBackgroundView.backgroundColor = .whiteColor()
        textFieldBackgroundView.layer.cornerRadius = textEntryHeight / 2

        messageEntryView.backgroundColor = UIColor(RGBred: 242, green: 242, blue: 242)

        sendButton.setTitle("Send", forState: .Normal)
        sendButton.titleLabel?.font = UIFont.boldSystemFontOfSize(17)

        sendButton.addTarget(self, action: #selector(submitMessage), forControlEvents: .TouchUpInside)
        textField.addTarget(self, action: #selector(updateButtonState), forControlEvents: [.ValueChanged, .AllEditingEvents])
    }

    private func setupHierarchy() {
        addSubview(tableView)
        addSubview(messageEntryView)
        messageEntryView.addSubview(textFieldBackgroundView)
        messageEntryView.addSubview(sendButton)
        textFieldBackgroundView.addSubview(textField)
    }

    private func setupLayout() {

        tableView.pinToSuperviewTop()
        messageEntryView.attachToBottomOf(tableView)
        messageEntryView.pinToSuperviewBottom()

        tableView.pinToSuperviewLeading()
        tableView.pinToSuperviewTrailing()

        messageEntryView.pinToSuperviewLeading()
        messageEntryView.pinToSuperviewTrailing()

        textFieldBackgroundView.addHeightConstraint(withConstant: textEntryHeight, priority: UILayoutPriorityDefaultHigh)

        textFieldBackgroundView.pinToSuperviewTop(withConstant: verticalSpacing)
        textFieldBackgroundView.pinToSuperviewBottom(withConstant: verticalSpacing)

        textFieldBackgroundView.pinToSuperviewLeading(withConstant: horizontalSpacing)
        sendButton.attachToRightOf(textFieldBackgroundView, withConstant: horizontalSpacing)
        sendButton.pinToSuperviewTrailing(withConstant: horizontalSpacing)

        sendButton.alignVerticalCenter(withView: textField)

        textField.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)
        textField.pinToSuperviewEdges(withInsets: UIEdgeInsets(top: 12, left: 24, bottom: 12, right: 12))
    }
}

extension ChatView: ChatDisplayer {
    func display(chat: Chat, forUser user: User) {
        tableViewManager.updateTableView(tableView, withChat: chat, andUser: user)
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

    func updateButtonState() {
        sendButton.enabled = !messageText.isEmpty
    }

    func submitMessage() {
        guard !messageText.isEmpty else {
            return
        }

        actionListener?.submitMessage(messageText)
        textField.text = ""
    }

    var messageText: String {
        return textField.text?.stringByTrimmingCharactersInSet(.whitespaceCharacterSet()) ?? ""
    }
}

extension ChatView: UITextFieldDelegate {
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        submitMessage()
        return true
    }
}
