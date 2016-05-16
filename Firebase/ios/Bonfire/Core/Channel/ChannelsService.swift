import Foundation
import RxSwift

protocol ChannelsService {
    func channels(forUser user: User) -> Observable<[Channel]>
}
