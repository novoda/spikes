//
//  TweetsViewController.swift
//  dialog-actions-for-voiceover
//
//  Created by Ataul Munim on 10/08/2017.
//  Copyright © 2017 Novoda. All rights reserved.
//

import UIKit

let cellIdentifier: String = "tweetCell"

class TweetsViewController: UIViewController {
    
    let tweetsProvider = TweetsProvider()
    
    var tableView = UITableView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(TweetCell.self, forCellReuseIdentifier: cellIdentifier)
        let tableViewAdapter = TweetsTableViewAdapter()
        tableView.dataSource = tableViewAdapter
        
        tweetsProvider.fetchTweets(completion: { tweets in
            tableViewAdapter.update(tweets)
            self.tableView.reloadData()
        })
    }
}

class TweetsTableViewAdapter: NSObject, UITableViewDataSource {
    
    var tweets: [Tweet] = []
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: TweetCell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier) as! TweetCell
        // TODO: bind tweet to view
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tweets.count
    }
    
    func update(_ tweets: [Tweet]) {
        self.tweets = tweets
    }
}
