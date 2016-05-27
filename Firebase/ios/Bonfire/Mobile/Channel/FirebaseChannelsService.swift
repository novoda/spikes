import Foundation

enum DatabaseWriteResult<T> {
    case Success(T)
    case Error(ErrorType)
}
