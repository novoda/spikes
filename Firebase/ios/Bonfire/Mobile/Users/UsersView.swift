import UIKit

final class UsersView: UIView {

    private let tableView = UITableView()
    private let tableViewManager = UsersTableViewManager()

    weak var actionListener: UsersActionListener?

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
        tableViewManager.actionListener = self
    }

    private func setupLayout() {
        addSubview(tableView)

        tableView.pinToSuperviewTop()
        tableView.pinToSuperviewBottom()
        tableView.pinToSuperviewLeading()
        tableView.pinToSuperviewTrailing()
    }

}

extension UsersView: UsersDisplayer {

    func display(allUsers: Users, channelOwners: Users) {
        tableViewManager.updateTableView(tableView, withUsers: allUsers, withOwners: channelOwners)
    }

}


extension UsersView: UsersTableViewActionListener {
    func didSelectUser(user: User) {
        actionListener?.addOwner(user)
    }

    func didDeselectUser(user: User) {
        actionListener?.removeOwner(user)
    }
}
