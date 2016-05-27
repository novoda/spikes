import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

enum ChannelCreation {
    case Idle
    case Busy
    case Successful
    case Error
}

enum DatabaseWriteResult<T> {
    case Success(T)
    case Error(ErrorType)
}

func convertToFirebaseOwners(owners: [User]) -> AnyObject {
    var dic = [String: Bool]()
    owners.forEach({ val in
        dic[val.id] = true
    })
    return dic
}
