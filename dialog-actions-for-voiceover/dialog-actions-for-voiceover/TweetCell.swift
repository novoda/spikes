import UIKit

class TweetCell: UITableViewCell {

    var avatarImageView = UIImageView()
    var usernameLabel = UILabel()
    var bodyLabel = UILabel()
    var timeLabel = UILabel()
    var buttonsGroup = UIView()
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
//        UIAccessibilityIsVoiceOverRunning()
    }

    private func setupHierarchy() {
        addSubview(avatarImageView)
        addSubview(timeLabel)
        addSubview(usernameLabel)
        addSubview(bodyLabel)

        addSubview(replyButton)
        addSubview(retweetButton)
        addSubview(likeButton)

        subviews.forEach { view in
            view.translatesAutoresizingMaskIntoConstraints = false
        }
    }

    private func styleViews() {
        replyButton.setTitle("reply", for: .normal)
        replyButton.backgroundColor = UIColor.darkGray

        retweetButton.setTitle("rt", for: .normal)
        retweetButton.backgroundColor = UIColor.black

        likeButton.setTitle("like", for: .normal)
        likeButton.backgroundColor = UIColor.darkGray

        avatarImageView.backgroundColor = UIColor.white

        usernameLabel.numberOfLines = 1

        timeLabel.numberOfLines = 1
        timeLabel.setContentHuggingPriority(UILayoutPriorityDefaultHigh, for: .horizontal)

        bodyLabel.numberOfLines = 0
        bodyLabel.lineBreakMode = .byWordWrapping
    }

    private func layoutViews() {
        layoutAvatar()
        layoutUsername()
        layoutTimeLabel()
        layoutUsername()
        layoutBody()

        NSLayoutConstraint.activate([
            match(view: replyButton, attribute: .leading, otherView: avatarImageView, otherAttribute: .trailing),
            NSLayoutConstraint(
                    item: replyButton,
                    attribute: .trailing,
                    relatedBy: .lessThanOrEqual,
                    toItem: retweetButton,
                    attribute: .leading,
                    multiplier: 1,
                    constant: 0
            ),
            match(view: replyButton, attribute: .top, otherView: bodyLabel, otherAttribute: .bottom)
        ])

        NSLayoutConstraint.activate([
            match(view: retweetButton, attribute: .leading, otherView: replyButton, otherAttribute: .trailing),
            NSLayoutConstraint(
                    item: retweetButton,
                    attribute: .trailing,
                    relatedBy: .lessThanOrEqual,
                    toItem: likeButton,
                    attribute: .leading,
                    multiplier: 1,
                    constant: 0
            ),
            match(view: retweetButton, attribute: .top, otherView: bodyLabel, otherAttribute: .bottom)
        ])

        NSLayoutConstraint.activate([
            match(view: likeButton, attribute: .leading, otherView: retweetButton, otherAttribute: .trailing),
            matchTweetCell(attribute: .trailing, view: likeButton),
            match(view: retweetButton, attribute: .top, otherView: bodyLabel, otherAttribute: .bottom)
        ])
    }

    private func layoutAvatar() {
        NSLayoutConstraint.activate([
            matchTweetCell(attribute: .leading, view: avatarImageView),
            matchTweetCell(attribute: .top, view: avatarImageView),
            constrainValue(view: avatarImageView, attribute: .width, value: 40),
            constrainValue(view: avatarImageView, attribute: .height, value: 40)
        ])
    }

    private func layoutUsername() {
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
            matchTweetCell(attribute: .top, view: usernameLabel),
            match(view: usernameLabel, attribute: .leading, otherView: avatarImageView, otherAttribute: .trailing),
        ])
    }

    private func layoutTimeLabel() {
        NSLayoutConstraint.activate([
            matchTweetCell(attribute: .trailing, view: timeLabel),
            matchTweetCell(attribute: .top, view: timeLabel),
            match(view: timeLabel, attribute: .leading, otherView: usernameLabel, otherAttribute: .trailing),
        ])
    }

    private func layoutBody() {
        NSLayoutConstraint.activate([
            match(view: bodyLabel, attribute: .leading, otherView: avatarImageView, otherAttribute: .trailing),
            matchTweetCell(attribute: .right, view: bodyLabel),
            match(view: bodyLabel, attribute: .top, otherView: usernameLabel, otherAttribute: .bottom),
            matchTweetCell(attribute: .bottom, view: bodyLabel),
            NSLayoutConstraint(
                    item: bodyLabel,
                    attribute: .bottom,
                    relatedBy: .lessThanOrEqual,
                    toItem: replyButton,
                    attribute: .top,
                    multiplier: 1,
                    constant: 0
            )
        ])
    }

    private func constrainValue(view: UIView, attribute: NSLayoutAttribute, value: CGFloat) -> NSLayoutConstraint {
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

    private func match(view: UIView, attribute: NSLayoutAttribute, otherView: UIView, otherAttribute: NSLayoutAttribute) -> NSLayoutConstraint {
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

    private func matchTweetCell(attribute: NSLayoutAttribute, view: UIView) -> NSLayoutConstraint {
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
        loadImage(url: tweet.user.avatarUrl)
    }

    private func loadImage(url: String) {
        if let url = NSURL(string: url), let data = NSData(contentsOf: url as URL) {
            avatarImageView.image = UIImage(data: data as Data)
        }
    }
}
