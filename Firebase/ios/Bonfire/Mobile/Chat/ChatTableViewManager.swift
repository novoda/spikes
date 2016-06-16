import UIKit

final class ChatTableViewManager: NSObject, UITableViewDataSource, UITableViewDelegate {
    private var messages = [Message]()
    private var currentUser: User?

    func updateTableView(tableView: UITableView, withChat chat: Chat, andUser user: User) {
        let animated = messages.count > 0

        messages = chat.messages
        currentUser = user

        tableView.reloadData()

        if messages.count > 0 {
            let indexPath = NSIndexPath(forItem: messages.count - 1, inSection: 0)
            tableView.scrollToRowAtIndexPath(indexPath, atScrollPosition: .Bottom, animated: animated)
        }
    }

    func setupTableView(tableView: UITableView) {
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 100
        tableView.separatorStyle = .None

        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(MessageCell)
        tableView.register(MyMessageCell)
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messages.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let message = messages[indexPath.row]

        if message.author == currentUser {
            let cell: MyMessageCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.updateWithMessage(message)
            return cell
        }

        let cell: MessageCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
        cell.updateWithMessage(message)
        return cell
    }

    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
}
