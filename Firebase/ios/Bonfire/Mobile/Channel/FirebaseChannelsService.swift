import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private func channelsIndex() -> FIRDatabaseReference {
        return firebase.child("channels-global-index")
    }

    func channels() -> Observable<[Channel]> {
        return Observable.create { observer in
            let handle = self.channelsIndex().observeEventType(.Value, withBlock: { snapshot in
                let firebaseChannels = snapshot.children.allObjects
                let channels = firebaseChannels.map{$0.value}.map(Channel.init)
                observer.onNext(channels)
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }
        }
    }

}
