import UIKit

final class ChatTableViewManager: NSObject, UITableViewDataSource, UITableViewDelegate {
    private var messages = [Message]()

    func updateTableView(tableView: UITableView, withChat chat: Chat) {
        messages = chat.messages
        tableView.reloadData()
//        let indexPath = NSIndexPath(forItem: messages.count - 1, inSection: 0)
//        tableView.scrollToRowAtIndexPath(indexPath, atScrollPosition: .Bottom, animated: true)
    }

    func setupTableView(tableView: UITableView) {
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 100

        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ChatCell)
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messages.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell: ChatCell = tableView.dequeueReusableCell(forIndexPath: indexPath)

        let message = messages[indexPath.row]
        cell.updateWithMessage(message)

        return cell
    }

    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
}
