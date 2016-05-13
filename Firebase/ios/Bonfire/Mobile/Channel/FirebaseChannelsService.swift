import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private var _channels: FIRDatabaseReference {
        return firebase.child("channels")
    }

    func channels() -> Observable<[Channel]> {
        return Observable.create { observer in
            let handle = self._channels.observeEventType(.Value, withBlock: { snapshot in
                // return channels
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }
        }
    }

}
