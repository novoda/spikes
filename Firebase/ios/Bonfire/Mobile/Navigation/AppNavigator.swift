import UIKit

final class AppNavigator: Navigator {

    let navigationController = UINavigationController()

    func toChat() {
        let chatViewController = ChatViewController.withDependencies()
        navigationController.setViewControllers([chatViewController], animated: true)
    }

}
