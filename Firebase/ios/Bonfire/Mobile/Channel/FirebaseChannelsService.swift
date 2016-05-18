import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private func channelsIndex() -> FIRDatabaseReference {
        return firebase.child("public-channels-index")
    }

    private func privateChannelsIndex(forUser user: User) -> FIRDatabaseReference {
        return firebase.child("private-channels-index").child(user.id)
    }

    func channels(forUser user: User) -> Observable<[Channel]> {
        return Observable.combineLatest(publicChannels(), privateChannels(forUser: user)) { publicChannels, privateChannels in
            return publicChannels + privateChannels
        }
    }

    func createPublicChannel(withName name: String) {
        channelsIndex().childByAutoId().setValue(name)
    }

    private func publicChannels() -> Observable<[Channel]> {
        return Observable.create({ observer in

            let handle = self.channelsIndex().observeEventType(.Value, withBlock: { snapshot in
                let firebaseChannels: [Observable<Channel>] = snapshot.children.allObjects.map{$0.key!}.map(self.channel)
                observer.onNext(firebaseChannels)
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }

        }).flatMap(mergeToArray)
    }

    private func privateChannels(forUser user: User) -> Observable<[Channel]> {
        // Observable<[Observable<Channel>]>

        return Observable.create({ observer in

            let handle = self.privateChannelsIndex(forUser: user).observeEventType(.Value, withBlock: { snapshot in
                let firebaseChannelKeys = snapshot.children.allObjects.map{$0.key!}
                let firebaseChannels: [Observable<Channel>] = firebaseChannelKeys.map(self.channel)
                observer.onNext(firebaseChannels)
            })

            return AnonymousDisposable() {
                self.firebase.removeObserverWithHandle(handle)
            }

        }).flatMap(mergeToArray)
    }

    private func channel(withKey key: String) -> Observable<Channel> {
        return Observable.create({ observer in
            self.firebase.child("channels/\(key)").observeSingleEventOfType(.Value, withBlock: { snapshot in
                let channel = try! Channel(firebaseValue: snapshot.value!)
                observer.on(.Next(channel))
                observer.on(.Completed)
            })

            return AnonymousDisposable() {}
        })
    }

}
