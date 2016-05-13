import UIKit

final class AppNavigator: Navigator {

    let navigationController = UINavigationController()

    func toChannels() {
        let channelsViewController = ChannelsViewController.withDependencies()
        navigationController.setViewControllers([channelsViewController], animated: true)
    }

    func toChat() {
        let chatViewController = ChatViewController.withDependencies()
        navigationController.setViewControllers([chatViewController], animated: true)
    }

}
