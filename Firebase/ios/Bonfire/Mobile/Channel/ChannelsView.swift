import UIKit

final class ChannelsView: UIView {
    private let tableView = UITableView()
    private let tableViewManager = ChannelsTableViewManager()
    private weak var actionListener: ChannelsActionListener?

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

extension ChannelsView: ChannelsDisplayer {
    func display(channels: [Channel]) {
        tableViewManager.updateTableView(tableView, withChannels: channels)
    }

    func attach(actionListener: ChannelsActionListener) {
        self.actionListener = actionListener
    }

    func detach(actionListener: ChannelsActionListener) {
        self.actionListener = nil
    }
}

extension ChannelsView: ChannelsTableViewActionListener {
    func didSelectChannel(channel: Channel) {
        actionListener?.viewChannel(channel)
    }
}
