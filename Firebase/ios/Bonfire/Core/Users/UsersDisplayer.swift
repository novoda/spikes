import Foundation

protocol UsersActionListener: class {
    func addOwner(user: User)
    func removeOwner(user: User)
}

protocol UsersDisplayer: class {
    func display(allUsers: [User], channelOwners: [User])
    weak var actionListener: UsersActionListener? { get set }
}
