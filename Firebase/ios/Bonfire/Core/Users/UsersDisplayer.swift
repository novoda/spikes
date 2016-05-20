import Foundation

protocol UsersActionListener: class {
    func addUsers(users: [User])
}

protocol UsersDisplayer: class {
    func display(users: [User])
    weak var actionListener: UsersActionListener? { get set }
}
