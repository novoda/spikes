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
        let usersViewController = UsersViewController.withDependencies(channel: channel)
        navigationController.pushViewController(usersViewController, animated: true)
    }

    func toWelcome(senderID: String?) {
        let welcomeViewController = WelcomeViewController.withDependencies(senderID: senderID)
        let container = UINavigationController()
        container.viewControllers = [welcomeViewController]
        navigationController.presentViewController(container, animated: true, completion: nil)
    }

    func dismissWelcome() {
        navigationController.dismissViewControllerAnimated(true, completion: nil)
    }

    func showShareSheet(activityItems: [AnyObject]) {
        let activityController = UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
        navigationController.presentViewController(activityController, animated: true, completion: nil)
    }
}
