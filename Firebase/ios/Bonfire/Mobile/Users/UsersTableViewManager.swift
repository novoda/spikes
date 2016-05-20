import UIKit

final class UsersTableViewManager: NSObject, UITableViewDataSource, UITableViewDelegate {
    private var users = [User]()

    func updateTableView(tableView: UITableView, withUsers users: [User]) {
        self.users = users
        tableView.reloadData()
    }

    func setupTableView(tableView: UITableView) {
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 100

        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UserCell)
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return users.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell: UserCell = tableView.dequeueReusableCell(forIndexPath: indexPath)

        let user = users[indexPath.row]
        cell.updateWithUser(user)

        return cell
    }

    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
}


