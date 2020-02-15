import Foundation
import Kitura
import KituraNet
import XCTest
import HeliumLogger
import LoggerAPI

@testable import Application

class RouteTests: XCTestCase {
    static var port: Int!
    static var allTests : [(String, (RouteTests) -> () throws -> Void)] {
        return [
            ("testGetStatic", testGetStatic)
        ]
    }

    override func setUp() {
        super.setUp()

        HeliumLogger.use()
        do {
            print("------------------------------")
            print("------------New Test----------")
            print("------------------------------")

            let app = try App()
            RouteTests.port = app.cloudEnv.port
            try app.postInit()
            Kitura.addHTTPServer(onPort: RouteTests.port, with: app.router)
            Kitura.start()
        } catch {
            XCTFail("Couldn't start Application test server: \(error)")
        }
    }

    override func tearDown() {
        Kitura.stop()
        super.tearDown()
    }

    func testGetStatic() {

        let printExpectation = expectation(description: "The /route will serve static HTML content.")

        URLRequest(forTestWithMethod: "GET")?
            .sendForTestingWithKitura { data, statusCode in
                if let getResult = String(data: data, encoding: String.Encoding.utf8){
                    XCTAssertEqual(statusCode, 200)
                    XCTAssertTrue(getResult.contains("<html"))
                    XCTAssertTrue(getResult.contains("</html>"))
                } else {
                    XCTFail("Return value from / was nil!")
                }

                printExpectation.fulfill()
        }

        waitForExpectations(timeout: 10.0, handler: nil)
    }
    
    func testHealthRoute() {
        let printExpectation = expectation(description: "The /health route will print UP, followed by a timestamp.")
        
        URLRequest(forTestWithMethod: "GET", route: "health")?
            .sendForTestingWithKitura { data, statusCode in
                if let getResult = String(data: data, encoding: String.Encoding.utf8) {
                    XCTAssertEqual(statusCode, 200)
                    XCTAssertTrue(getResult.contains("UP"), "UP not found in the result.")
                    let date = Date()
                    let calendar = Calendar.current
                    let yearString = String(describing: calendar.component(.year, from: date))
                    XCTAssertTrue(getResult.contains(yearString), "Failed to create String from date. Date is either missing or incorrect.")
                } else {
                    XCTFail("Unable to convert request Data to String.")
                }
                printExpectation.fulfill()
        }
        waitForExpectations(timeout: 10.0, handler: nil)
    }
}
    
    




private extension URLRequest {

    init?(forTestWithMethod method: String, route: String = "", body: Data? = nil) {
        if let url = URL(string: "http://127.0.0.1:\(RouteTests.port)/" + route){
            self.init(url: url)
            addValue("application/json", forHTTPHeaderField: "Content-Type")
            httpMethod = method
            cachePolicy = .reloadIgnoringCacheData
            if let body = body {
                httpBody = body
            }
        } else {
            XCTFail("URL is nil...")
            return nil
        }
    }

    func sendForTestingWithKitura(fn: @escaping (Data, Int) -> Void) {

        guard let method = httpMethod, var path = url?.path, let headers = allHTTPHeaderFields else {
            XCTFail("Invalid request params")
            return
        }

        if let query = url?.query {
            path += "?" + query
        }

        let requestOptions: [ClientRequest.Options] = [.method(method), .hostname("localhost"), .port(8080), .path(path), .headers(headers)]

        let req = HTTP.request(requestOptions) { resp in

            if let resp = resp, resp.statusCode == HTTPStatusCode.OK || resp.statusCode == HTTPStatusCode.accepted {
                do {
                    var body = Data()
                    try resp.readAllData(into: &body)
                    fn(body, resp.statusCode.rawValue)
                } catch {
                    print("Bad JSON document received from Kitura-Starter.")
                }
            } else {
                if let resp = resp {
                    print("Status code: \(resp.statusCode)")
                    var rawUserData = Data()
                    do {
                        let _ = try resp.read(into: &rawUserData)
                        let str = String(data: rawUserData, encoding: String.Encoding(rawValue: String.Encoding.utf8.rawValue))
                        print("Error response from Kitura-Starter: \(String(describing: str))")
                    } catch {
                        print("Failed to read response data.")
                    }
                }
            }
        }
        if let dataBody = httpBody {
            req.end(dataBody)
        } else {
            req.end()
        }
    }
}
