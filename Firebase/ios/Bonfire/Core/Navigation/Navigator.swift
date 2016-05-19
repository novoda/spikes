import Foundation

protocol Navigator {
    func toChannels()
    func toChat(channel: Channel)
    func toCreateChannel()
}
