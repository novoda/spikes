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
    var tweets: [Tweet] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)
        view.backgroundColor = UIColor.white
        
        NSLayoutConstraint.activate([
            topLayoutGuide.bottomAnchor.constraint(equalTo: tableView.topAnchor),
            bottomLayoutGuide.topAnchor.constraint(equalTo: tableView.bottomAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
        
        tableView.register(TweetCell.self, forCellReuseIdentifier: cellIdentifier)
        tableView.dataSource = self
        tableView.delegate = self
        
        
        
        tweetsProvider.fetchTweets(completion: { tweets in
            self.update(tweets)
            self.tableView.reloadData()
        })
    }
}

extension TweetsViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: TweetCell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! TweetCell
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
