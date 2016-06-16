//
//  SnapshotProcess.swift
//  Bonfire
//
//  Created by Yvette Cook on 07/06/2016.
//  Copyright © 2016 Novoda. All rights reserved.
//

import XCTest

class SnapshotProcess: XCTestCase {
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false

        let app = XCUIApplication()
        setupSnapshot(app)
        app.launch()
    }
    
    override func tearDown() {
        super.tearDown()
    }

    func testViewChannels() {
        sleep(10)
        snapshot("01Channels")
    }

    func testCreateChannel() {
        let app = XCUIApplication()
        sleep(10)
        app.navigationBars["Channels"].buttons["Compose"].tap()
        app.textFields["NameField"].tap()
        app.textFields["NameField"].typeText("😎")
        snapshot("02CreateChannel")
    }

    func testViewChat() {
        sleep(10)
        XCUIApplication().collectionViews.staticTexts["⚜️"].tap()
        snapshot("03ViewChat")
    }


}
