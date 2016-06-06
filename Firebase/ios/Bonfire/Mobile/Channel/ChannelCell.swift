import UIKit

final class ChannelCell: UICollectionViewCell {

    var emojiLabel = UILabel()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupLayout()
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setupViews() {
        self.backgroundColor = .whiteColor()

        emojiLabel.font = UIFont.systemFontOfSize(55)
        emojiLabel.textAlignment = .Center
    }

    func setupLayout() {
        addSubview(emojiLabel)

        emojiLabel.pinToSuperviewTop(withConstant: 16)
        emojiLabel.pinToSuperviewLeading(withConstant: 16)
        emojiLabel.pinToSuperviewTrailing(withConstant: 16)
        emojiLabel.pinToSuperviewBottom(withConstant: 16)
    }

    func updateWithChannel(channel: Channel) {
        emojiLabel.text = channel.name

        self.layer.borderWidth = 3
        self.layer.cornerRadius = 5
        
        if channel.access == .Private {
            self.layer.borderColor = UIColor.orangeColor().CGColor
        } else {
            self.layer.borderColor = UIColor.greenColor().CGColor
        }
    }
}
