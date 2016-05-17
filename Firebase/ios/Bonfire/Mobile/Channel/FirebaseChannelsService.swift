import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private func channelsIndex() -> FIRDatabaseReference {
        return firebase.child("channels-public-index")
    }

    private func privateChannelsIndex(forUser user: User) -> FIRDatabaseReference {
        return firebase.child("channels-private-index").child(user.id)
    }

    func channels(forUser user: User) -> Observable<[Channel]> {
        return Observable.combineLatest(publicChannels(), privateChannels(forUser: user)) { publicChannels, privateChannels in
            return publicChannels + privateChannels
        }
    }

    func publicChannels() -> Observable<[Channel]> {
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

    func privateChannels(forUser user: User) -> Observable<[Channel]> {
        return Observable.create { observer in
            let handle = self.privateChannelsIndex(forUser: user).observeEventType(.Value, withBlock: { snapshot in
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
