import Foundation
import Firebase
import FirebaseAnalytics
import FirebaseInstanceID
import FirebaseDatabase
import RxSwift

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

    func rx_readValue() -> Observable<FIRDataSnapshot> {
        return Observable.create({ observer in

            let handle = self.observeEventType(.Value, withBlock: { snapshot in
                observer.on(.Next(snapshot))
            })

            return AnonymousDisposable() {
                self.removeObserverWithHandle(handle)
            }
        })
    }

}