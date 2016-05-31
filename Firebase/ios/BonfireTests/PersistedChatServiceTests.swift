import XCTest
import RxSwift
import RxTests
@testable import Bonfire

class PersistedChatServiceTests: XCTestCase {

    struct MockChatDatabase: ChatDatabase {
        let chatObservable: TestableObservable<Chat>

        func chat(channel: Channel) -> Observable<Chat> {
            return chatObservable.asObservable()
        }

        func sendMessage(message: Message, channel: Channel) {}
    }

    func testThatItMapsChatObservableToDatabaseResultObservable() {
        // Given
        let channel =  Channel(name: "TestChannel", access: .Public)
        let chat = Chat(channel: channel, messages: [])

        let scheduler = TestScheduler(initialClock: 0)
        let testableObserver = scheduler.createObserver(DatabaseResult<Chat>)

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


}
