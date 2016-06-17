import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedUserServiceTests: XCTestCase {

    var scheduler: TestScheduler!
    var testableUsersObserver: TestableObserver<Users>!
    var mockUserDatabase: UserDatabase!
    var userService: PersistedUserService!

    let testUser1 = User(name: "TestUser1", identifier: "1", photoURL: nil)
    let testUser2 = User(name: "TestUser2", identifier: "2", photoURL: nil)

    var disposeBag: DisposeBag!

    override func setUp() {
        super.setUp()

        scheduler = TestScheduler(initialClock: 0)
        disposeBag = DisposeBag()
        testableUsersObserver = scheduler.createObserver(Users)
        mockUserDatabase = MockUserDatabase(scheduler: scheduler)

        userService = PersistedUserService(userDatabase: mockUserDatabase)
    }

    struct MockUserDatabase: UserDatabase {

        let scheduler: TestScheduler

        init(scheduler: TestScheduler) {
            self.scheduler = scheduler
        }

        func observeUsers() -> Observable<Users> {
            return scheduler.singleEventAndHang(Users(users: [ User(name: "TestUser1", identifier: "1", photoURL: nil), User(name: "TestUser2", identifier: "2", photoURL: nil) ]))
        }

        func readUserFrom(userID: String) -> Observable<User> { return Observable.empty() }
        func writeCurrentUser(user: User) { }
        func observeUser(userID: String) -> Observable<User> { return Observable.empty() }

    }

    override func tearDown() {
        super.tearDown()
    }

    func testThatServiceCanReturnAllUsers() {
        // When
        userService.allUsers().subscribe(testableUsersObserver)
        scheduler.start()

        // Then
        let expectedUsers = Users(users: [testUser1, testUser2])

        let expectedEvents = [
            next(0, expectedUsers)
        ]

        XCTAssertEqual(testableUsersObserver.events, expectedEvents)
    }


}
