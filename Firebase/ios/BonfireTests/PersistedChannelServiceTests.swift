import XCTest
import RxSwift
import RxTests
@testable import Bonfire


class Remembrancer {
    var callStack = [MethodCall]()
}

struct MethodCall {
    let identifier: String
    let arguments: [MethodArgument]
}

extension MethodCall: Equatable {}

func ==(lhs: MethodCall, rhs: MethodCall) -> Bool {
    return lhs.identifier == rhs.identifier &&
        lhs.arguments.map({$0.asMethodArgument()}) == rhs.arguments.map({$0.asMethodArgument()})
}

protocol MethodArgument {
    func asMethodArgument() -> String
}

extension Channel: MethodArgument {
    func asMethodArgument() -> String {
        return "Channel: \(name) / \(access.rawValue)"
    }
}

class PersistedChannelServiceTests: XCTestCase {

    var scheduler: TestScheduler!
    var remembrance: Remembrancer!
    var testableChannelsObserver: TestableObserver<Channels>!
    var testableUsersObserver: TestableObserver<Users>!
    var mockChannelDatabase: ChannelsDatabase!
    var mockUserDatabase: UserDatabase!
    var channelService: PersistedChannelsService!

    let testUser1 = User(name: "TestUser1", id: "1", photoURL: nil)
    let testUser2 = User(name: "TestUser2", id: "2", photoURL: nil)

    let publicChannel = Channel(name: "💣", access: .Public)
    let privateChannel = Channel(name: "🙈", access: .Private)

    override func setUp() {
        super.setUp()
        scheduler = TestScheduler(initialClock: 0)
        remembrance = Remembrancer()
        testableChannelsObserver = scheduler.createObserver(Channels)
        testableUsersObserver = scheduler.createObserver(Users)

        mockChannelDatabase = MockChannelDatabase(scheduler: scheduler, remembrance: remembrance)
        mockUserDatabase = MockUserDatabase(scheduler: scheduler)

        channelService = PersistedChannelsService(channelsDatabase: mockChannelDatabase, userDatabase: mockUserDatabase)
    }

    override func tearDown() {
        super.tearDown()
    }

    struct MockUserDatabase: UserDatabase {

        let scheduler: TestScheduler

        init(scheduler: TestScheduler) {
            self.scheduler = scheduler
        }

        func observeUsers() -> Observable<Users> { return Observable.empty() }

        func readUserFrom(userID: String) -> Observable<User> {
            if userID == "1" {
                return scheduler.singleEventAndComplete(User(name: "TestUser1", id: "1", photoURL: nil))
            } else if userID == "2" {
                return scheduler.singleEventAndComplete(User(name: "TestUser2", id: "2", photoURL: nil))
            } else {
                return Observable.empty()
            }
        }

        func writeCurrentUser(user: User) {}
    }

    class MockChannelDatabase: ChannelsDatabase {

        let scheduler: TestScheduler
        let remembrance: Remembrancer

        init(scheduler: TestScheduler, remembrance: Remembrancer) {
            self.scheduler = scheduler
            self.remembrance = remembrance
        }

        func observePublicChannelIds() -> Observable<[String]> {
            return scheduler.singleEventAndHang(["💣"])
        }

        func observePrivateChannelIdsFor(user: User) -> Observable<[String]> {
            if user.id == "1" {
                return scheduler.singleEventAndHang(["🙈"])
            } else {
                return Observable.empty()
            }
        }

        func readChannelFor(channelName: String) -> Observable<Channel> {
            if channelName == "💣" {
                return scheduler.singleEventAndComplete(Channel(name: "💣", access: .Public))
            } else if channelName == "🙈" {
                return scheduler.singleEventAndComplete(Channel(name: "🙈", access: .Private))
            } else {
                return Observable.empty()
            }
        }

        func writeChannel(newChannel: Channel) -> Observable<Channel> {
            remembrance.callStack.append(MethodCall(identifier: "ChannelsDatabase - writeChannel", arguments: [newChannel]))
            return Observable.just(newChannel)
        }

        func writeChannelToPublicChannelIndex(newChannel: Channel) -> Observable<Channel> {
            remembrance.callStack.append(MethodCall(identifier: "ChannelsDatabase - writeChannelToPublicChannelIndex", arguments: [newChannel]))
            return Observable.empty()
        }

        func addOwnerToPrivateChannel(user: User, channel: Channel) -> Observable<Channel> { return Observable.empty() }

        func removeOwnerFromPrivateChannel(user: User, channel: Channel) -> Observable<Channel> { return Observable.empty() }

        func addChannelToUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> { return Observable.empty() }

        func removeChannelFromUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> { return Observable.empty() }

        func observeOwnerIdsFor(channel: Channel) -> Observable<[String]> {
            if channel.name == "🙈" {
                return scheduler.singleEventAndHang(["1", "2"])
            } else {
                return Observable.empty()
            }
        }

    }

    func testThatItReturnsChannelsForUser() {
        // When
        channelService.channels(forUser: testUser1).subscribe(testableChannelsObserver)
        scheduler.start()

        // Then
        let channels = [publicChannel, privateChannel]
        let expectedEvents = [
            next(0, Channels(channels: channels))
        ]

        XCTAssertEqual(testableChannelsObserver.events, expectedEvents)
    }


    func testThatItReturnsUsersForChannel() {
        // When
        channelService.users(forChannel: privateChannel).subscribe(testableUsersObserver)
        scheduler.start()

        // Then
        let users = [testUser1, testUser2]
        let expectedEvents = [
            next(0, Users(users: users))
        ]
        
        XCTAssertEqual(testableUsersObserver.events, expectedEvents)
    }

    func testThatItCallsTheRightThingsForCreatingAPublicChannel() {
        let disposeBag = DisposeBag()
        channelService.createPublicChannel(withName: "🐣").subscribe().addDisposableTo(disposeBag)

        let expectedChannel = Channel(name: "🐣", access: .Public)
        let expectedMethodStack = [
            MethodCall(identifier: "ChannelsDatabase - writeChannel", arguments: [expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - writeChannelToPublicChannelIndex", arguments: [expectedChannel])
        ]

        XCTAssertEqual(remembrance.callStack, expectedMethodStack)
    }
}

