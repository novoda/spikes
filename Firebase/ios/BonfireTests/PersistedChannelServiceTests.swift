import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedChannelServiceTests: XCTestCase {

    var scheduler: TestScheduler!
    var testableChannelsObserver: TestableObserver<Channel>!
    var testableUsersObserver: TestableObserver<Users>!

    let testUser1 = User(name: "TestUser1", id: "1", photoURL: nil)
    let testUser2 = User(name: "TestUser2", id: "2", photoURL: nil)

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

        let scheduler: TestScheduler

        init(scheduler: TestScheduler) {
            self.scheduler = scheduler
        }

        func observeUsers() -> Observable<Users> {
            return Observable.empty()
        }

        func readUserFrom(userID: String) -> Observable<User> {
            if userID == "1" {
                return scheduler.createColdObservable([
                    next(0, User(name: "TestUser1", id: "1", photoURL: nil)),
                    completed(0)
                    ]).asObservable()
            } else if userID == "2" {
                return scheduler.createColdObservable([
                    next(0, User(name: "TestUser2", id: "2", photoURL: nil)),
                    completed(0)
                    ]).asObservable().debug("wtf")
            } else {
                return Observable.empty()
            }
        }

        func writeCurrentUser(user: User) {}
    }

    struct MockChannelDatabase: ChannelsDatabase {

        let scheduler: TestScheduler

        init(scheduler: TestScheduler) {
            self.scheduler = scheduler
        }

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
            if channel.name == "Test Channel" {
                return scheduler.createHotObservable([
                    next(0, ["1", "2"])
                    ]).asObservable()
            } else {
                return Observable.empty()
            }
        }

    }

    func testThatItReturnsChannelsForUser() {
        let user = User(name: "Test User", id: "1", photoURL: nil)


    }


    func testThatItReturnsUsersForChannel() {
        // Given
        let channel = Channel(name: "Test Channel", access: .Private)

        let mockChannelDatabase = MockChannelDatabase(scheduler: scheduler)
        let mockUserDatabase = MockUserDatabase(scheduler: scheduler)

        let channelService = PersistedChannelsService(channelsDatabase: mockChannelDatabase, userDatabase: mockUserDatabase)

        // When
        channelService.users(forChannel: channel).subscribe(testableUsersObserver)
        scheduler.start()

        // Then
        let users = [testUser1, testUser2]
        let expectedEvents = [
            next(0, Users(users: users))
        ]
        
        XCTAssertEqual(testableUsersObserver.events, expectedEvents)
    }
}
