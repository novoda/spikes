import Foundation

struct Channels {
    let channels: [Channel]

    var count: Int {
        return channels.count
    }

    func sorted() -> Channels {
        return Channels(channels: self.channels.sort({$0.name < $1.name}))
    }
}

extension Channels {
    init() {
        self.channels = []
    }
}

// MARK - Equatable

extension Channels: Equatable {}

func ==(lhs: Channels, rhs: Channels) -> Bool {
    return lhs.channels == rhs.channels
}
