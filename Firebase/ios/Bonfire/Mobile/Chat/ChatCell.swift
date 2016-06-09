import UIKit
import RxSwift
import RxCocoa

struct HTTPImageServiceError: ErrorType {}

final class ChatCell: UITableViewCell {

    let authorLabel = UILabel()
    let timestampLabel = UILabel()
    let photoView = UIImageView()
    let messageLabel = UILabel()

    let verticalMargin: CGFloat = 5
    let horizontalMargin: CGFloat = 16
    let imageSize: CGFloat = 28

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
        messageLabel.numberOfLines = 0
        authorLabel.numberOfLines = 0
        messageLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)
        authorLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)
        timestampLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)
        timestampLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Horizontal)
        timestampLabel.setContentHuggingPriority(UILayoutPriorityRequired, forAxis: .Horizontal)

        photoView.contentMode = .ScaleAspectFit
        photoView.layer.cornerRadius = imageSize/2
        photoView.layer.masksToBounds = true

        authorLabel.font = UIFont.boldSystemFontOfSize(12)
        timestampLabel.font = UIFont.italicSystemFontOfSize(10)
        timestampLabel.textColor = .lightGrayColor()
        messageLabel.font = UIFont.systemFontOfSize(12)
    }

    func setupLayout() {
        addSubview(photoView)
        addSubview(messageLabel)
        addSubview(authorLabel)
        addSubview(timestampLabel)

        photoView.pinToSuperviewLeading(withConstant: horizontalMargin)
        messageLabel.attachToRightOf(photoView, withConstant: 10)
        messageLabel.pinToSuperviewTrailing(withConstant: horizontalMargin)

        photoView.pinToSuperviewTop(withConstant: verticalMargin)
        photoView.pinToSuperviewBottom(withConstant: verticalMargin, priority: UILayoutPriorityDefaultHigh)

        authorLabel.attachToRightOf(photoView, withConstant: 10)
        authorLabel.pinToSuperviewTop(withConstant: verticalMargin)

        timestampLabel.attachToRightOf(authorLabel, withConstant: 10)
        timestampLabel.pinToSuperviewTop(withConstant: verticalMargin)
        timestampLabel.pinToSuperviewTrailing(withConstant: horizontalMargin)

        messageLabel.attachToBottomOf(authorLabel, withConstant: 6)
        messageLabel.pinToSuperviewBottom(withConstant: verticalMargin)

        photoView.addHeightConstraint(withConstant: imageSize)
        photoView.addWidthConstraint(withConstant: imageSize)
    }

    override func prepareForReuse() {
        disposeBag = nil
        super.prepareForReuse()
    }

    func updateWithMessage(message: Message) {
        let date = NSDate(timeIntervalSince1970: NSTimeInterval(message.timestamp / 1000))
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateStyle = .NoStyle
        dateFormatter.timeStyle = .ShortStyle

        authorLabel.text = message.author.name
        messageLabel.text = message.body
        timestampLabel.text = dateFormatter.stringFromDate(date)

        if let url = message.author.photoURL {
            setUserPhoto(url)
        }
    }

    private func setUserPhoto(url: NSURL) {
        disposeBag = DisposeBag()

        photoView.hidden = true
        imageForURL(url)
            .observeOn(MainScheduler.instance)
            .subscribeNext({ [weak self] image in
                self?.photoView.hidden = false
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
