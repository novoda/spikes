import UIKit

final class ChannelCell: UITableViewCell {
    func updateWithChannel(channel: Channel) {
        textLabel?.text = channel.name
    }
}
