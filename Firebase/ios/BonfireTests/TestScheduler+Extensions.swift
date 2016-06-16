import Foundation
import RxSwift
import RxTests

extension TestScheduler {
    func singleEventAndComplete<Element>(element: Element) -> Observable<Element> {
        return self.createColdObservable([
            next(0, element),
            completed(0)
            ]).asObservable()
    }

    func singleEventAndHang<Element>(element: Element) -> Observable<Element> {
        return self.createHotObservable([
            next(0, element)
            ]).asObservable()
    }
}
