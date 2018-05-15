//
//  FastlaneExampleUITests.swift
//  FastlaneExampleUITests
//
//  Created by Berta Devant on 15/03/2018.
//  Copyright © 2018 Berta Devant. All rights reserved.
//

import XCTest

class FastlaneExampleUITests: XCTestCase {
        
    override func setUp() {
        super.setUp()
        
        // Put setup code here. This method is called before the invocation of each test method in the class.
        
        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false
        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        XCUIApplication().launch()
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {
        XCUIApplication().buttons["Button"].tap()
        XCTAssertEqual(XCUIApplication().buttons["Button"].title, "Pressed")
    }
    
}
