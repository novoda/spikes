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
        let chatObservable: Observable<Chat>
        let sendMessageExpectation: ((Message, Channel) -> Void)?

        init(chatObservable: Observable<Chat>,
             sendMessageExpectation: ((Message, Channel) -> Void)? = nil) {
            self.chatObservable = chatObservable
            self.sendMessageExpectation = sendMessageExpectation
        }

        func chat(channel: Channel) -> Observable<Chat> {
            return chatObservable
        }

        func sendMessage(message: Message, channel: Channel) {
            sendMessageExpectation?(message, channel)
        }
    }

    func testThatItMapsChatObservableToDatabaseResultObservable() {
        // Given
        let chat = Chat(channel: channel, messages: [])

        let testChatEvents = scheduler.singleEventAndHang(chat)

        let chatDatabase = MockChatDatabase(chatObservable: testChatEvents)
        let chatService = PersistedChatService(chatDatabase: chatDatabase)

        // When
        chatService.chat(channel).subscribe(testableObserver)
        scheduler.start()

        // Then
        let expectedEvents = [
            next(0, DatabaseResult.Success(chat))
        ]

        XCTAssertEqual(testableObserver.events, expectedEvents)
    }

    func testThatItMapsAnErrorToDatabaseResult() {
        // Given
        let errorEvent: TestableObservable<Chat> = scheduler.createHotObservable([
            error(1, TestErrorType())
            ])

        let chatDatabase = MockChatDatabase(chatObservable: errorEvent.asObservable())
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
    
    func testThatItCallsTheSendMessageOnTheDatabase() {
        // Given
        let user = User(name: "TestUser", identifier: "1", photoURL: nil)
        let message = Message(author: user, body: "Test message")

        let expectation = self.expectationWithDescription("testThatItCallsTheSendMessageOnTheDatabase")

        let chatDatabase = MockChatDatabase(chatObservable: Observable.empty()) { msg, chnl in
            // Then
            XCTAssertEqual(msg, message)
            XCTAssertEqual(chnl, self.channel)
            expectation.fulfill()
        }

        let chatService = PersistedChatService(chatDatabase: chatDatabase)

        // When
        chatService.sendMessage(message, channel: channel)
        self.waitForExpectationsWithTimeout(0.1, handler: nil)
    }
    
}
