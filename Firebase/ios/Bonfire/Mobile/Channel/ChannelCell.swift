import UIKit

final class ChannelCell: UICollectionViewCell {

    var emojiLabel = UILabel()
    let privateImage = UIImageView(image: UIImage(named: "lock"))

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

        privateImage.contentMode = .ScaleAspectFill
    }

    func setupLayout() {
        addSubview(emojiLabel)
        addSubview(privateImage)

        emojiLabel.pinToSuperviewTop(withConstant: 16)
        emojiLabel.pinToSuperviewLeading(withConstant: 16)
        emojiLabel.pinToSuperviewTrailing(withConstant: 16)
        emojiLabel.pinToSuperviewBottom(withConstant: 16)

        privateImage.pinToSuperviewTop()
        privateImage.pinToSuperviewTrailing()
        privateImage.addWidthConstraint(withConstant: 15)
        privateImage.addHeightConstraint(withConstant: 15)
    }

    func updateWithChannel(channel: Channel) {
        emojiLabel.text = channel.name

        if channel.access == .Public {
            privateImage.hidden = true
        } else {
            privateImage.hidden = false

        }
    }
}
