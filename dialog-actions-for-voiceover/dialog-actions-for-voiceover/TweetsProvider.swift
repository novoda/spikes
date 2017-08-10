//
//  TweetsProvider.swift
//  dialog-actions-for-voiceover
//
//  Created by Ataul Munim on 10/08/2017.
//  Copyright © 2017 Novoda. All rights reserved.
//

import Foundation

class TweetsProvider {
    
    func fetchTweets(completion: @escaping (_ tweets: [Tweet]) -> Void) {
        completion(createTweets())
    }
    
    private func createTweets() -> [Tweet] {
        return [
            Tweet(id: "1", username: "u1", body: "sdfsf", avatarUrl: "https://robohash.org/sdfsdfsdf", time: "12:12"),
            Tweet(id: "2", username: "u2", body: "dsfgdsfgdfsg", avatarUrl: "https://robohash.org/sdfsdfsdfsfdsf", time: "12:12")
        ]
    }
}
