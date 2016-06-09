import UIKit

protocol ReusableView: class {
    static var defaultReuseIdentifier: String { get }
}

extension ReusableView where Self: UIView {
    static var defaultReuseIdentifier: String {
        return String(self)
    }
}

extension UITableViewCell: ReusableView {}

extension UITableView {

    func register<T: UITableViewCell where T: ReusableView>(type: T.Type) {
        let reuseIdentifier = type.defaultReuseIdentifier
        registerClass(type.self, forCellReuseIdentifier: reuseIdentifier)
    }

    func dequeueReusableCell<T: UITableViewCell where T: ReusableView>(forIndexPath indexPath: NSIndexPath) -> T {
        guard let cell = dequeueReusableCellWithIdentifier(T.defaultReuseIdentifier, forIndexPath: indexPath) as? T else {
            fatalError("Could not dequeue cell with identifier: \(T.defaultReuseIdentifier)")
        }

        return cell
    }
}

extension UICollectionViewCell: ReusableView {}

extension UICollectionView {

    func register<T: UICollectionViewCell where T: ReusableView>(type: T.Type) {
        let reuseIdentifier = type.defaultReuseIdentifier
        registerClass(type.self, forCellWithReuseIdentifier: reuseIdentifier)
    }

    func dequeueReusableCell<T: UICollectionViewCell where T: ReusableView>(forIndexPath indexPath: NSIndexPath) -> T {
        guard let cell = dequeueReusableCellWithReuseIdentifier(T.defaultReuseIdentifier, forIndexPath: indexPath) as? T else {
            fatalError("Could not dequeue cell with identifier: \(T.defaultReuseIdentifier)")
        }

        return cell
    }
}
