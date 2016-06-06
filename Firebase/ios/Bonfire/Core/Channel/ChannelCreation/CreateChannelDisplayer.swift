import Foundation

protocol CreateChannelActionListener: class {
    func createChannel(withName name: String, privateChannel: Bool)
}

protocol CreateChannelDisplayer: class {
    func displayError(error: ErrorType)
    weak var actionListener: CreateChannelActionListener? { get set }
}
