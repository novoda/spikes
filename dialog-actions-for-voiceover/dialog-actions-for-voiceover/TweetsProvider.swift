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
            Tweet(id: "1", username: "mrfox", body: "Are you cussing with me?", avatarUrl: "https://robohash.org/mrfox", time: "23:12"),
            Tweet(id: "2", username: "kristofferson", body: "...I've got mixed feelings about that", avatarUrl: "https://robohash.org/kristofferson", time: "22:20"),
            Tweet(id: "3", username: "mrsfox", body: "If what I think is happening, is happening...It'd better not be", avatarUrl: "https://robohash.org/mrsfox", time: "20:56"),
            Tweet(id: "4", username: "ash", body: "No you're not. You're disloyal.", avatarUrl: "https://robohash.org/ash", time: "19:11"),
            Tweet(id: "5", username: "mrfox", body: "I understand what you're saying, and your comments are valuable, but I'm gonna ignore your advice.", avatarUrl: "https://robohash.org/mrfox", time: "15:04"),
            Tweet(id: "6", username: "badger", body: "If you're gonna cuss with somebody, you're not gonna cuss with me, you little cuss!", avatarUrl: "https://robohash.org/badger", time: "14:44"),
            Tweet(id: "7", username: "mrfox", body: "you are without a doubt the five and a half most wonderful wild animals I've ever met in my life", avatarUrl: "https://robohash.org/mrfox", time: "14:32"),
            Tweet(id: "8", username: "mole", body: "I'm sick of your double talk, we have rights!", avatarUrl: "https://robohash.org/mole", time: "12:08"),
            Tweet(id: "9", username: "mrfox", body: "Pensez-vous que l'hiver sera rude?", avatarUrl: "https://robohash.org/mrfox", time: "12:05"),
            Tweet(id: "10", username: "mrsfox", body: "You know, you really are... fantastic.", avatarUrl: "https://robohash.org/mrsfox", time: "10:37")
        ]
        
    }
}
