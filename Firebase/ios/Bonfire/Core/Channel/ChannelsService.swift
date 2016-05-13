import Foundation
import RxSwift

protocol ChannelsService {
    func channels() -> Observable<[Channel]>
}
