import Foundation

protocol ChannelsActionListener: class {
    func viewChannel(channel: Channel)
}

protocol ChannelsDisplayer {
    func display(channels: [Channel])
    func attach(actionListener: ChannelsActionListener)
    func detach(actionListener: ChannelsActionListener)
}
