import UIKit

class SenderInfoView: UIView {
    let profileImage = UIImageView()
    let userNameLabel = UILabel()

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
        profileImage.layer.cornerRadius = 43

        userNameLabel.font = UIFont.systemFontOfSize(14)
        userNameLabel.numberOfLines = 0
        userNameLabel.textAlignment = .Center
    }

    func setupLayout() {
        addSubview(profileImage)
        addSubview(userNameLabel)

        profileImage.addHeightConstraint(withConstant: 86)
        profileImage.addWidthConstraint(withConstant: 86)
        profileImage.alignVerticalCenterWithSuperview(withMultiplier: 0.9)
        profileImage.alignHorizontalCenterWithSuperview()

        userNameLabel.attachToBottomOf(profileImage, withConstant: 16)
        userNameLabel.pinToSuperviewLeading(withConstant: 40)
        userNameLabel.pinToSuperviewTrailing(withConstant: 40)
    }

    func updateWithProfileImage(image: UIImage) {
        profileImage.image = image
    }

    func updateWithUserName(name: String) {
        userNameLabel.text = "\(name) invited you to Bonfire "
    }
}
