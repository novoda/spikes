import UIKit

final class LogoView: UIView  {

    let logoImage = UIImageView()
    let logoImageDimensions: CGFloat = 110

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
        logoImage.image = UIImage(named: "logo")
    }

    func setupLayout() {
        addSubview(logoImage)

        logoImage.addHeightConstraint(withConstant: logoImageDimensions)
        logoImage.addWidthConstraint(withConstant: logoImageDimensions)
        logoImage.alignVerticalCenterWithSuperview()
        logoImage.alignHorizontalCenterWithSuperview()
    }
    
}
