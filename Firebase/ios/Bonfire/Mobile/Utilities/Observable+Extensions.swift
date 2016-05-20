import Foundation
import RxSwift

func mergeToArray<T>(observables: [Observable<T>]) -> Observable<[T]> {
    return observables.toObservable().merge().toArray()
}
