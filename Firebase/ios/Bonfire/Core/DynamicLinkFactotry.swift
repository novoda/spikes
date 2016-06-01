import Foundation

protocol DynamicLinkFactory {
    func inviteLinkFromUser(user: User) -> NSURL
}
