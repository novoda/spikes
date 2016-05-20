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

extension FIRDatabaseReference {

    func rx_write(value: AnyObject) -> Observable<Void> {
        return Observable.create({ observer in
            self.setValue(value, withCompletionBlock: { error, firebase in
                if let error = error {
                    observer.on(.Error(error))
                } else {
                    observer.on(.Next())
                }
                observer.on(.Completed)
            })

            return AnonymousDisposable {}
        })
    }
}

class FirebaseChannelsService: ChannelsService {

    let firebase = FIRDatabase.database().reference()

    private func channelsInfo(withKey key: String) -> FIRDatabaseReference {
        return firebase.child("channels/\(key)")
    }

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

    func createPublicChannel(withName name: String) -> Observable<DatabaseWriteResult<Channel>> {
        let name = name.stringByTrimmingCharactersInSet(.whitespaceCharacterSet())

        let channel = Channel(name: name, access: .Public)
        return self.channelsInfo(withKey: name).rx_write(channel.asFirebaseValue())
            .flatMap({self.channelsIndex().child(channel.name).rx_write(true)})
            .map({DatabaseWriteResult.Success(channel)})
            .catchError({Observable.just(DatabaseWriteResult.Error($0))})
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
            self.channelsInfo(withKey: key).observeSingleEventOfType(.Value, withBlock: { snapshot in
                if let channelFirebaseValue = snapshot.value,
                    let channel = try? Channel(firebaseValue: channelFirebaseValue) {
                    observer.on(.Next(channel))
                } else {
                    print("Couldn't find \(key)")
                }
                observer.on(.Completed)
            })
            
            return AnonymousDisposable() {}
        })
    }
    
}
