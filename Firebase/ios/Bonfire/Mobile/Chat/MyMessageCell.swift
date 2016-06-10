import UIKit
import RxSwift
import RxCocoa

final class MyMessageCell: UITableViewCell {
    let timestampLabel = UILabel()
    let photoView = UIImageView()
    let smallDot = UIView()
    let bigDot = UIView()
    let bubbleView = UIView()
    let messageLabel = UILabel()

    let smallDotSize: CGFloat = 6
    let bigDotSize: CGFloat = 12
    let bigDotOverlap: CGFloat = 7
    let verticalSpacing: CGFloat = 5
    let verticalMargin: CGFloat = 10
    let horizontalMargin: CGFloat = 16
    let imageSize: CGFloat = 36

    let messageFont = UIFont.systemFontOfSize(12)
    let timestampFont = UIFont.systemFontOfSize(10)
    let bubbleColor = UIColor(RGBred: 254, green: 133, blue: 80)

    var disposeBag: DisposeBag! = nil

    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupViews()
        setupHierarchy()
        setupLayout()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setupViews() {
        messageLabel.numberOfLines = 0

        photoView.contentMode = .ScaleAspectFit
        photoView.layer.cornerRadius = imageSize/2
        photoView.layer.masksToBounds = true

        timestampLabel.font = timestampFont
        timestampLabel.textColor = .darkGrayColor()
        timestampLabel.textAlignment = .Right

        messageLabel.font = messageFont
        messageLabel.textColor = .whiteColor()

        bubbleView.backgroundColor = bubbleColor
        smallDot.backgroundColor = bubbleColor
        bigDot.backgroundColor = bubbleColor
        let cornerRadius: CGFloat = verticalSpacing * 2 + messageFont.lineHeight/2
        bubbleView.layer.cornerRadius = cornerRadius
        smallDot.layer.cornerRadius = smallDotSize/2
        bigDot.layer.cornerRadius = bigDotSize/2
    }

    func setupHierarchy() {
        addSubview(smallDot)
        addSubview(bigDot)
        addSubview(bubbleView)
        addSubview(photoView)
        addSubview(timestampLabel)

        bubbleView.addSubview(messageLabel)
    }

    func setupLayout() {
        smallDot.addHeightConstraint(withConstant: smallDotSize)
        smallDot.addWidthConstraint(withConstant: smallDotSize)
        smallDot.alignVerticalCenter(withView: photoView)

        bigDot.addHeightConstraint(withConstant: bigDotSize)
        bigDot.addWidthConstraint(withConstant: bigDotSize)
        bigDot.alignVerticalCenter(withView: smallDot)

        messageLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)

        timestampLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)
        timestampLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Horizontal)

        messageLabel.setContentHuggingPriority(UILayoutPriorityRequired, forAxis: .Horizontal)
        messageLabel.setContentCompressionResistancePriority(UILayoutPriorityRequired, forAxis: .Vertical)

        bubbleView.pinToSuperviewLeading(withConstant: horizontalMargin, priority: UILayoutPriorityDefaultHigh)
        bigDot.attachToRightOf(bubbleView, withConstant: -bigDotOverlap)
        smallDot.attachToRightOf(bigDot, withConstant: horizontalMargin/4)
        photoView.attachToRightOf(smallDot, withConstant: horizontalMargin/2)
        photoView.pinToSuperviewTrailing(withConstant: horizontalMargin)

        photoView.attachToBottomOf(timestampLabel, withConstant: verticalSpacing)
        photoView.pinToSuperviewBottom(withConstant: verticalMargin, priority: UILayoutPriorityDefaultHigh)

        timestampLabel.pinToSuperviewLeading(withConstant: horizontalMargin)
        timestampLabel.pinToSuperviewTop(withConstant: verticalMargin)
        timestampLabel.alignTrailing(withView: messageLabel)

        bubbleView.attachToBottomOf(timestampLabel, withConstant: verticalSpacing)
        bubbleView.pinToSuperviewBottom(withConstant: verticalMargin)

        photoView.addHeightConstraint(withConstant: imageSize)
        photoView.addWidthConstraint(withConstant: imageSize)

        messageLabel.pinToSuperviewEdges(withInsets: UIEdgeInsets(
            top: verticalSpacing,
            left: horizontalMargin - (bigDotSize - bigDotOverlap),
            bottom: verticalSpacing,
            right: horizontalMargin
            )
        )
    }

    override func prepareForReuse() {
        disposeBag = nil
        super.prepareForReuse()
    }

    func updateWithMessage(message: Message) {
        let date = NSDate(timeIntervalSince1970: NSTimeInterval(message.timestamp / 1000))
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateStyle = .MediumStyle
        dateFormatter.timeStyle = .ShortStyle

        messageLabel.text = message.body
        timestampLabel.text = dateFormatter.stringFromDate(date)

        if let url = message.author.photoURL {
            setUserPhoto(url)
        }
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
