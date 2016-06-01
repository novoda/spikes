import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedChannelServiceTests: XCTestCase {

    var scheduler: TestScheduler!
    var testableChannelsObserver: TestableObserver<Channel>!
    var testableUsersObserver: TestableObserver<Users>!

    let testUser = User(name: "TestUser", id: "1", photoURL: nil)

    override func setUp() {
        super.setUp()
        scheduler = TestScheduler(initialClock: 0)
        testableChannelsObserver = scheduler.createObserver(Channel)
        testableUsersObserver = scheduler.createObserver(Users)
    }
    
    override func tearDown() {
        super.tearDown()
    }

    struct MockUserDatabase: UserDatabase {
        func observeUsers() -> Observable<Users> {
            return Observable.empty()
        }

        func readUserFrom(userID: String) -> Observable<User> {
            return Observable.just(User(name: "TestUser", id: "1", photoURL: nil))
        }

        func writeCurrentUser(user: User) {}
    }

    struct MockChannelDatabase: ChannelsDatabase {
        func observePublicChannelIds() -> Observable<[String]> {
            return Observable.empty()
        }

        func observePrivateChannelIdsFor(user: User) -> Observable<[String]> {
            return Observable.empty()
        }

        func readChannelFor(channelName: String) -> Observable<Channel> {
            return Observable.empty()
        }

        func writeChannel(newChannel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func writeChannelToPublicChannelIndex(newChannel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func addOwnerToPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func removeOwnerFromPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func addChannelToUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func removeChannelFromUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
            return Observable.empty()
        }

        func observeOwnerIdsFor(channel: Channel) -> Observable<[String]> {
            return Observable.empty()
        }

    }

    func testThatItReturnsUsersForChannel() {
        // Given
        let channel = Channel(name: "Test Channel", access: .Public)
        let mockChannelDatabase = MockChannelDatabase()
        let mockUserDatabase = MockUserDatabase()

        let channelService = PersistedChannelsService(channelsDatabase: mockChannelDatabase, userDatabase: mockUserDatabase)

        // When
        channelService.users(forChannel: channel).subscribe(testableUsersObserver)
        scheduler.start()

        // Then
        let users = [testUser]
        let expectedEvents = [
            next(1, Users(users: users))
        ]

        XCTAssertEqual(testableUsersObserver.events, expectedEvents)
    }
}
