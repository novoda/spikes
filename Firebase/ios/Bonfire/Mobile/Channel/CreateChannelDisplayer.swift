import Foundation

protocol CreateChannelActionListener {
    func createChannel(withName name: String)
}

protocol CreateChannelDisplayer {
    func displayError(error: ErrorType)
}
