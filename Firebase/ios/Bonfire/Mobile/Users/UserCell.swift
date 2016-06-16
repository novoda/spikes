import UIKit
import RxSwift
import RxCocoa

final class UserCell: UITableViewCell {

    let userLabel = UILabel()
    let photoView = UIImageView()

    let verticalMargin: CGFloat = 14
    let horizontalMargin: CGFloat = 16
    let imageSize: CGFloat = 36

    var disposeBag: DisposeBag! = nil

    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupLayout()
        setupViews()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setupViews() {
        userLabel.numberOfLines = 0

        photoView.contentMode = .ScaleAspectFit
        photoView.layer.cornerRadius = imageSize / 2
        photoView.layer.masksToBounds = true

        userLabel.font = UIFont.systemFontOfSize(14)
    }

    func setupLayout() {
        addSubview(photoView)
        addSubview(userLabel)

        photoView.pinToSuperviewLeading(withConstant: horizontalMargin)
        photoView.pinToSuperviewTop(withConstant: verticalMargin)
        photoView.pinToSuperviewBottom(withConstant: verticalMargin, priority: UILayoutPriorityDefaultHigh)

        userLabel.attachToRightOf(photoView, withConstant: horizontalMargin)
        userLabel.pinToSuperviewTrailing(withConstant: horizontalMargin)
        userLabel.alignVerticalCenterWithSuperview()

        photoView.addHeightConstraint(withConstant: imageSize)
        photoView.addWidthConstraint(withConstant: imageSize)
    }

    override func prepareForReuse() {
        disposeBag = nil
        super.prepareForReuse()
    }

    func updateWithUser(user: User, selected: Bool) {
        userLabel.text = user.name

        if let url = user.photoURL {
            setUserPhoto(url)
        }

        backgroundColor = selected ? BonfireColors.veryLightPink : .clearColor()
    }

    private func setUserPhoto(url: NSURL) {
        disposeBag = DisposeBag()

        photoView.image = UIImage(named: "ic_person")
        imageForURL(url)
            .observeOn(MainScheduler.instance)
            .subscribeNext({ [weak self] image in
                self?.photoView.image = image
                }).addDisposableTo(disposeBag)
    }

    func imageForURL(url: NSURL) -> Observable<UIImage?> {
        let request = NSURLRequest(URL: url)
        return NSURLSession.sharedSession().rx_data(request).map { data in
            guard let image = UIImage(data: data) else {
                throw HTTPImageServiceError()
            }

            return image
        }
    }
}
