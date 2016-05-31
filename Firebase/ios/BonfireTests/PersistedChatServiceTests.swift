import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedChatServiceTests: XCTestCase {

    let channel =  Channel(name: "TestChannel", access: .Public)

    var scheduler: TestScheduler!
    var testableObserver: TestableObserver<DatabaseResult<Chat>>!

    override func setUp() {
        super.setUp()
        scheduler = TestScheduler(initialClock: 0)
        testableObserver = scheduler.createObserver(DatabaseResult<Chat>)
    }


    struct TestErrorType: ErrorType {}

    struct MockChatDatabase: ChatDatabase {
        let chatObservable: TestableObservable<Chat>

        func chat(channel: Channel) -> Observable<Chat> {
            return chatObservable.asObservable()
        }

        func sendMessage(message: Message, channel: Channel) {}
    }

    func testThatItMapsChatObservableToDatabaseResultObservable() {
        // Given
        let chat = Chat(channel: channel, messages: [])

        let testChatEvents = scheduler.createHotObservable([
            next(1, chat)
            ])

        let chatDatabase = MockChatDatabase(chatObservable: testChatEvents)
        let chatService = PersistedChatService(chatDatabase: chatDatabase)

        // When
        chatService.chat(channel).subscribe(testableObserver)
        scheduler.start()

        // Then
        let expectedEvents = [
            next(1, DatabaseResult.Success(chat))
        ]

        XCTAssertEqual(testableObserver.events, expectedEvents)
    }

    func testThatItMapsAnErrorToDatabaseResult() {
        // Given
        let errorEvent: TestableObservable<Chat> = scheduler.createHotObservable([
            error(1, TestErrorType())
            ])

        let chatDatabase = MockChatDatabase(chatObservable: errorEvent)
        let chatService = PersistedChatService(chatDatabase: chatDatabase)

        // When
        chatService.chat(channel).subscribe(testableObserver)
        scheduler.start()

        // Then
        let expectedEvents: [Recorded<Event<DatabaseResult<Chat>>>] = [
            next(1, DatabaseResult.Error(TestErrorType())),
            completed(1)
        ]

        XCTAssertEqual(testableObserver.events, expectedEvents)
    }

}
