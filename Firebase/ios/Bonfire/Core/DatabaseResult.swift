import Foundation

enum DatabaseResult<T> {
    case Success(T)
    case Error(ErrorType)
}
