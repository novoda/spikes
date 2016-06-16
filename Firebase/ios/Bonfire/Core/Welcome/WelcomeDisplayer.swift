import Foundation

protocol WelcomeActionListener: class {
    func welcomeDone()
}

protocol WelcomeDisplayer {
    func display(user: User)
    weak var actionListener: WelcomeActionListener? { get set }
}
