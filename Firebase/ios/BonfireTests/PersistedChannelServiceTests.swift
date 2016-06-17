import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedChannelServiceTests: XCTestCase {

    var scheduler: TestScheduler!
    var remembrancer: Remembrancer!
    var testableChannelsObserver: TestableObserver<Channels>!
    var testableUsersObserver: TestableObserver<Users>!
    var mockChannelDatabase: ChannelsDatabase!
    var mockUserDatabase: UserDatabase!
    var channelService: PersistedChannelsService!

    let testUser1 = User(name: "TestUser1", identifier: "1", photoURL: nil)
    let testUser2 = User(name: "TestUser2", identifier: "2", photoURL: nil)

    let publicChannel = Channel(name: "💣", access: .Public)
    let privateChannel = Channel(name: "🙈", access: .Private)

    var disposeBag: DisposeBag!


    override func setUp() {
        super.setUp()
        scheduler = TestScheduler(initialClock: 0)
        remembrancer = Remembrancer()
        disposeBag = DisposeBag()
        testableChannelsObserver = scheduler.createObserver(Channels)
        testableUsersObserver = scheduler.createObserver(Users)

        mockChannelDatabase = MockChannelDatabase(scheduler: scheduler, remembrance: remembrancer)
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
                return scheduler.singleEventAndComplete(User(name: "TestUser1", identifier: "1", photoURL: nil))
            } else if userID == "2" {
                return scheduler.singleEventAndComplete(User(name: "TestUser2", identifier: "2", photoURL: nil))
            } else {
                return Observable.empty()
            }
        }

        func writeCurrentUser(user: User) {}
        func observeUser(userID: String) -> Observable<User> { return Observable.empty() }
    }

    class MockChannelDatabase: ChannelsDatabase {

        let scheduler: TestScheduler
        let remembrancer: Remembrancer

        init(scheduler: TestScheduler, remembrance: Remembrancer) {
            self.scheduler = scheduler
            self.remembrancer = remembrance
        }

        func observePublicChannelIds() -> Observable<[String]> {
            return scheduler.singleEventAndHang(["💣"])
        }

        func observePrivateChannelIdsFor(user: User) -> Observable<[String]> {
            if user.identifier == "1" {
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
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - writeChannel", arguments: [newChannel]))
            return Observable.just(newChannel)
        }

        func writeChannelToPublicChannelIndex(newChannel: Channel) -> Observable<Channel> {
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - writeChannelToPublicChannelIndex", arguments: [newChannel]))
            return Observable.just(newChannel)
        }

        func addOwnerToPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - addOwnerToPrivateChannel", arguments: [user, channel]))
            return Observable.just(channel)
        }

        func removeOwnerFromPrivateChannel(user: User, channel: Channel) -> Observable<Channel> {
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - removeOwnerFromPrivateChannel", arguments: [user, channel]))
            return Observable.just(channel)
        }

        func addChannelToUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - addChannelToUserPrivateChannelIndex", arguments: [user, channel]))
            return Observable.just(channel)
        }

        func removeChannelFromUserPrivateChannelIndex(user: User, channel: Channel) -> Observable<Channel> {
            remembrancer.callStack.append(MethodCall(identifier: "ChannelsDatabase - removeChannelFromUserPrivateChannelIndex", arguments: [user, channel]))

            return Observable.empty()
        }

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
        channelService.createPublicChannel(withName: "🐣").subscribe().addDisposableTo(disposeBag)

        let expectedChannel = Channel(name: "🐣", access: .Public)
        let expectedMethodStack = [
            MethodCall(identifier: "ChannelsDatabase - writeChannel", arguments: [expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - writeChannelToPublicChannelIndex", arguments: [expectedChannel])
        ]

        XCTAssertEqual(remembrancer.callStack, expectedMethodStack)
    }

    func testThatItCallsTheRightThingsForCreatingAPrivateChannel() {
        channelService.createPrivateChannel(withName: "🙈", owner: testUser1).subscribe().addDisposableTo(disposeBag)

        let expectedChannel = privateChannel
        let expectedMethodStack = [
            MethodCall(identifier: "ChannelsDatabase - addOwnerToPrivateChannel", arguments: [testUser1, expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - writeChannel", arguments: [expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - addChannelToUserPrivateChannelIndex", arguments: [testUser1, expectedChannel])
        ]

        XCTAssertEqual(remembrancer.callStack, expectedMethodStack)
    }

    func testThatItCallsTheRightThingsForAddingOwnerToChannel() {
        channelService.addOwner(testUser1, toPrivateChannel: privateChannel).subscribe().addDisposableTo(disposeBag)

        let expectedChannel = privateChannel
        let expectedMethodStack = [
            MethodCall(identifier: "ChannelsDatabase - addOwnerToPrivateChannel", arguments: [testUser1, expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - addChannelToUserPrivateChannelIndex", arguments: [testUser1, expectedChannel])
        ]

        XCTAssertEqual(remembrancer.callStack, expectedMethodStack)

    }


    func testThatItCallsTheRightThingsForRemovingOwnerFromChannel() {
        channelService.removeOwner(testUser1, fromPrivateChannel: privateChannel).subscribe().addDisposableTo(disposeBag)

        let expectedChannel = privateChannel
        let expectedMethodStack = [
            MethodCall(identifier: "ChannelsDatabase - removeOwnerFromPrivateChannel", arguments: [testUser1, expectedChannel]),
            MethodCall(identifier: "ChannelsDatabase - removeChannelFromUserPrivateChannelIndex", arguments: [testUser1, expectedChannel])
        ]

        XCTAssertEqual(remembrancer.callStack, expectedMethodStack)
        
    }
}

