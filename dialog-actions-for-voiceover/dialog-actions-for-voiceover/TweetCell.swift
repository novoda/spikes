import UIKit

class TweetCell: UITableViewCell {
    
    var avatarImageView = UIImageView()
    var usernameLabel = UILabel()
    var bodyLabel = UILabel()
    var timeLabel = UILabel()
    var replyButton = UIButton()
    var retweetButton = UIButton()
    var likeButton = UIButton()
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:)")
    }
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: .default, reuseIdentifier: reuseIdentifier)
        setupHierarchy()
        styleViews()
        layoutViews()
        
        avatarImageView.isAccessibilityElement = false
        timeLabel.isAccessibilityElement = false
        usernameLabel.isAccessibilityElement = false
        bodyLabel.isAccessibilityElement = false
        
        accessibilityLabel = "foo"
        
        
//        UIAccessibilityVoiceOverStatusChanged
//        NotificationCenter.default.addObserver(forName: UIAccessibilityVoiceOverStatusChanged., object: <#T##Any?#>, queue: <#T##OperationQueue?#>, using: <#T##(Notification) -> Void#>)
        UIAccessibilityIsVoiceOverRunning()
    }
    
    private func setupHierarchy() {
        addSubview(avatarImageView)
        addSubview(timeLabel)
        addSubview(usernameLabel)
        addSubview(bodyLabel)
        
//        addSubview(replyButton)
//        addSubview(retweetButton)
//        addSubview(likeButton)
        
        subviews.forEach { view in
            view.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    private func styleViews() {
        usernameLabel.text = "username"
        bodyLabel.text = "tweet body"
        timeLabel.text = "12:01"
        replyButton.setTitle("reply", for: .normal)
        retweetButton.setTitle("rt", for: .normal)
        likeButton.setTitle("like", for: .normal)
        avatarImageView.backgroundColor = UIColor.black
        
        usernameLabel.sizeToFit()
        usernameLabel.numberOfLines = 1
        
//        timeLabel.sizeToFit()
        timeLabel.numberOfLines = 1
        timeLabel.setContentHuggingPriority(UILayoutPriorityDefaultHigh, for: .horizontal)
        
        bodyLabel.sizeToFit()
        bodyLabel.numberOfLines = 0
        bodyLabel.lineBreakMode = .byWordWrapping
    }
    
    private func layoutViews() {
        NSLayoutConstraint.activate([
            constrainToTweetCell(attribute: .leading, view: avatarImageView),
            constrainToTweetCell(attribute: .top, view: avatarImageView),
            constrainToConstant(attribute: .width, value: 40, view: avatarImageView),
            constrainToConstant(attribute: .height, value: 40, view: avatarImageView)
        ])
        
        NSLayoutConstraint.activate([
            constrainToTweetCell(attribute: .trailing, view: timeLabel),
            constrainToTweetCell(attribute: .top, view: timeLabel),
            constrain(view: timeLabel, attribute: .leading, otherView: usernameLabel, otherAttribute: .trailing),
        ])
        
        NSLayoutConstraint.activate([
            NSLayoutConstraint(
                item: usernameLabel,
                attribute: .trailing,
                relatedBy: .lessThanOrEqual,
                toItem: timeLabel,
                attribute: .leading,
                multiplier: 1,
                constant: 0
            ),
            constrainToTweetCell(attribute: .top, view: usernameLabel),
            constrain(view: usernameLabel, attribute: .leading, otherView: avatarImageView, otherAttribute: .trailing),
        ])
        
        NSLayoutConstraint.activate([
            NSLayoutConstraint(
                item: bodyLabel,
                attribute: .leading,
                relatedBy: .equal,
                toItem: avatarImageView,
                attribute: .trailing,
                multiplier: 1,
                constant: 8
            ),
            constrainToTweetCell(attribute: .right, view: bodyLabel),
            constrain(view: bodyLabel, attribute: .top, otherView: usernameLabel, otherAttribute: .bottom),
            constrainToTweetCell(attribute: .bottom, view: bodyLabel),
        ])
    }
    
    private func constrainToConstant(attribute: NSLayoutAttribute, value: CGFloat, view: UIView) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: nil,
            attribute: .notAnAttribute,
            multiplier: 1,
            constant: value
        )
    }
    
    private func constrain(view: UIView, attribute: NSLayoutAttribute, otherView: UIView, otherAttribute: NSLayoutAttribute) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: otherView,
            attribute: otherAttribute,
            multiplier: 1,
            constant: 0
        )
    }
    
    private func constrainToTweetCell(attribute: NSLayoutAttribute, view: UIView) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: self,
            attribute: attribute,
            multiplier: 1,
            constant: 0
        )
    }
    
    func bind(_ tweet: Tweet) {
        usernameLabel.text = tweet.user.username
        bodyLabel.text = tweet.body
        timeLabel.text = tweet.time
        if let url = NSURL(string: tweet.user.avatarUrl), let data = NSData(contentsOf: url as URL) {
            avatarImageView.image = UIImage(data: data as Data)
        }
    }
}
