import UIKit

protocol AlertDelegate: class {
    func showAlert(title title: String?, message: String?)
}

extension UIViewController: AlertDelegate {
    func showAlert(title title: String?, message: String?) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .Alert)
        let okAction = UIAlertAction(title: "OK", style: .Default) { _ in
            alert.dismissViewControllerAnimated(true, completion: nil)
        }
        alert.addAction(okAction)

        self.presentViewController(alert, animated: true, completion: nil)
    }
}
