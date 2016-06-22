import UIKit

final class ChannelCell: UICollectionViewCell {

    var emojiLabel = UILabel()
    let privateImage = UIImageView(image: UIImage(named: "ic_lock"))
    let backgroundCircle = UIView()

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
        self.backgroundColor = .clearColor()

        emojiLabel.font = UIFont.systemFontOfSize(30)
        emojiLabel.textAlignment = .Center

        privateImage.contentMode = .ScaleAspectFill

        backgroundCircle.layer.cornerRadius = layer.frame.height / 2
        backgroundCircle.backgroundColor = UIColor.whiteColor()

    }

    func setupLayout() {
        addSubview(backgroundCircle)
        addSubview(emojiLabel)
        addSubview(privateImage)

        backgroundCircle.pinToSuperviewTop()
        backgroundCircle.pinToSuperviewEdges()
        backgroundCircle.pinToSuperviewBottom()

        emojiLabel.pinToSuperviewTop(withConstant: 16)
        emojiLabel.pinToSuperviewLeading(withConstant: 16)
        emojiLabel.pinToSuperviewTrailing(withConstant: 16)
        emojiLabel.pinToSuperviewBottom(withConstant: 16)

        privateImage.pinToSuperviewBottom()
        privateImage.pinToSuperviewTrailing()
        privateImage.addWidthConstraint(withConstant: 20)
        privateImage.addHeightConstraint(withConstant: 20)
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
