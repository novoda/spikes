import UIKit

final class AppNavigator: Navigator {

    let navigationController = UINavigationController()

    func toChannels() {
        let channelsViewController = ChannelsViewController.withDependencies()
        navigationController.setViewControllers([channelsViewController], animated: true)
    }

    func toChat(channel: Channel) {
        let chatViewController = ChatViewController.withDependencies()
        navigationController.setViewControllers([chatViewController], animated: true)
    }

}
