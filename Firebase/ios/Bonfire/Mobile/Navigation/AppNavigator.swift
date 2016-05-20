import UIKit

final class AppNavigator: Navigator {

    let navigationController = UINavigationController()

    func toChannels() {
        let channelsViewController = ChannelsViewController.withDependencies()
        navigationController.setViewControllers([channelsViewController], animated: true)
    }

    func toChat(channel: Channel) {
        let chatViewController = ChatViewController.withDependencies(channel: channel)
        navigationController.popToRootViewControllerAnimated(false)
        navigationController.pushViewController(chatViewController, animated: true)
    }

    func toCreateChannel() {
        let createChannelViewController = CreateChannelViewController.withDependencies()
        navigationController.pushViewController(createChannelViewController, animated: true)
    }

    func toAddUsers(channel: Channel) {
        print("Add some users in \(channel.name)")
    }
}
