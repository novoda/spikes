import Foundation

protocol UsersActionListener: class {
    func addUser(user: User)
}

protocol UsersDisplayer: class {
    func display(users: [User])
    weak var actionListener: UsersActionListener? { get set }
}
