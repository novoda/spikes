import Foundation

protocol CreateChannelActionListener: class {
    func createChannel(withName name: String)
}

protocol CreateChannelDisplayer: class {
    func displayError(error: ErrorType)
    weak var actionListener: CreateChannelActionListener? { get set }
}
