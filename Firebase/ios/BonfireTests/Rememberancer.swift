import Foundation
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

extension User: MethodArgument {
    func asMethodArgument() -> String {
        return "User: \(name)"
    }
}
