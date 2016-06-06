import UIKit

protocol ChannelsTableViewActionListener: class {
    func didSelectChannel(channel: Channel)
}

final class ChannelsTableViewManager: NSObject, UITableViewDataSource, UITableViewDelegate {

    private var channels = Channels()
    weak var actionListener: ChannelsTableViewActionListener?

    func updateTableView(tableView: UITableView, withChannels channels: Channels) {
        self.channels = channels
        tableView.reloadData()
    }

    func setupTableView(tableView: UITableView) {
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 100

        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ChannelCell)
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return channels.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell: ChannelCell = tableView.dequeueReusableCell(forIndexPath: indexPath)

        let channel = channels.channels[indexPath.row]
        cell.updateWithChannel(channel)

        return cell
    }

    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let channel = channels.channels[indexPath.row]
        actionListener?.didSelectChannel(channel)
    }

}
