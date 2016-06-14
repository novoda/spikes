import XCTest

class ReactTwitterUITests: XCTestCase {

  override func setUp() {
    super.setUp()
    continueAfterFailure = false

    XCUIApplication().launch()
  }

  override func tearDown() {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    super.tearDown()
  }

  func testLabelStartWithInitialState() {
    let app = XCUIApplication()

    self.goToUITestingScreen()

    let textLabel = app.staticTexts["label"]
    let text = textLabel.label

    XCTAssertEqual("No Clicks", text)
  }

  func testLabelStartUpdatesAfterFirstClick() {
    let app = XCUIApplication()

    self.goToUITestingScreen()

    app.otherElements["button"].tap()
    sleep(2)
    let textLabel = app.staticTexts["label"]
    let text = textLabel.label

    XCTAssertEqual("1 Clicks", text)
  }

  func goToUITestingScreen() {
    let app = XCUIApplication()
    if (app.alerts.count != 0) {
      app.alerts["“ReactTwitter” Would Like to Send You Notifications"].collectionViews.buttons["OK"].tap()
    }

    sleep(2) //wait for the splash screen

    //Go To Debug Screen
    app.otherElements["debug"].tap()

    //Go To UITesting Screen
    let debugList = app.otherElements["debug-list"]
    self.waitForElementToExist(debugList)
    debugList.otherElements["debug-uitesting-ios-scene-identifier"].tap()

    let textLabel = app.staticTexts["label"]
    self.waitForElementToExist(textLabel)

    let button = app.otherElements["button"]
    self.waitForElementToExist(button)
  }

  func waitForElementToExist(element: XCUIElement) {

    expectationForPredicate(NSPredicate(format: "exists == true"), evaluatedWithObject: element, handler: nil)
    waitForExpectationsWithTimeout(2, handler: nil)
  }
}
