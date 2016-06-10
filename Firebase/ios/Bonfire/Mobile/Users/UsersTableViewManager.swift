import UIKit

protocol UsersTableViewActionListener: class {
    func didSelectUser(user: User)
    func didDeselectUser(user: User)
}

final class UsersTableViewManager: NSObject, UITableViewDataSource, UITableViewDelegate {
    private var users = Users()
    private var owners = Users()
    weak var actionListener: UsersTableViewActionListener?

    func updateTableView(tableView: UITableView, withUsers users: Users, withOwners owners: Users) {
        self.users = users
        self.owners = owners
        tableView.reloadData()
    }

    func setupTableView(tableView: UITableView) {
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 100

        tableView.delegate = self
        tableView.dataSource = self
        tableView.allowsMultipleSelection = true

        tableView.register(UserCell)
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return users.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell: UserCell = tableView.dequeueReusableCell(forIndexPath: indexPath)

        let user = users.users[indexPath.row]
        let highlighted = owners.contains(user)
        cell.updateWithUser(user, selected: highlighted)

        return cell
    }

    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let user = users.users[indexPath.row]
        if owners.contains(user) {
            actionListener?.didDeselectUser(user)
        } else {
            actionListener?.didSelectUser(user)
        }

        tableView.deselectRowAtIndexPath(indexPath, animated: true)
    }
}
