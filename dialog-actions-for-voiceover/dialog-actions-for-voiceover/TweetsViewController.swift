import UIKit

let cellIdentifier: String = "tweetCell"

class TweetsViewController: UIViewController {
    
    let tweetsProvider = TweetsProvider()
    let tweetsAdapter = TweetsAdapter()
    
    var tableView = UITableView()
    var tweets: [Tweet] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)
        view.backgroundColor = UIColor.white
        constrainTableViewBoundsToViewPort()
        
        tableView.register(TweetCell.self, forCellReuseIdentifier: cellIdentifier)
        tableView.dataSource = tweetsAdapter
        tableView.delegate = tweetsAdapter
        
        tweetsProvider.fetchTweets(completion: { tweets in
            self.tweetsAdapter.update(tweets)
            self.tableView.reloadData()
        })
    }
    
    private func constrainTableViewBoundsToViewPort() {
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: topLayoutGuide.bottomAnchor),
            tableView.bottomAnchor.constraint(equalTo: bottomLayoutGuide.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }
}

class TweetsAdapter: NSObject, UITableViewDelegate, UITableViewDataSource {
    
    var tweets: [Tweet] = []
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: TweetCell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! TweetCell
        let tweet = tweets[indexPath.row]
        cell.bind(tweet)
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tweets.count
    }
    
    func update(_ tweets: [Tweet]) {
        self.tweets = tweets
    }
}
