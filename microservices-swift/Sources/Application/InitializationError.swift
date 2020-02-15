import Foundation

public struct InitializationError: Error {
    let message: String
    init(_ msg: String) {
        message = msg
    }
}

extension InitializationError: LocalizedError {
    public var errorDescription: String? {
        return message
    }
}
