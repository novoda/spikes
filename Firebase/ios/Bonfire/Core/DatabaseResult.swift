import Foundation

enum DatabaseResult<T: Equatable> {
    case Success(T)
    case Error(ErrorType)
}

// MARK - Equatable

extension DatabaseResult: Equatable {}

func ==<T>(lhs: DatabaseResult<T>, rhs: DatabaseResult<T>) -> Bool {
    switch (lhs, rhs) {
    case (.Success(let a), .Success(let b)): return a == b
    case (.Error, .Error): return true
    default: return false
    }
}
