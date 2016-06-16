import UIKit
import RxSwift
import RxCocoa


final class WelcomeView: UIView {

    private let bubblesBackground = BubbleBackgroundView()
    private var senderInfoView = SenderInfoView()
    private let welcomeMessageLabel = UILabel()
    private let loginButton = UIButton()

    weak var actionListener: WelcomeActionListener?

    private var imageRequestDisposeBag = DisposeBag()
    private let actionDisposeBag = DisposeBag()


    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupLayout()
        setupActions()
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        backgroundColor = BonfireColors.orange

        welcomeMessageLabel.numberOfLines = 0
        welcomeMessageLabel.text = "Get started and enjoy the emoji awesomeness"
        welcomeMessageLabel.textAlignment = .Center
        welcomeMessageLabel.font = UIFont.systemFontOfSize(16)
        welcomeMessageLabel.textColor = .whiteColor()

        loginButton.setTitle("Get Started", forState: .Normal)
        loginButton.setTitleColor(.darkGrayColor(), forState: .Normal)
        loginButton.layer.cornerRadius = 24
        loginButton.backgroundColor = .whiteColor()

        bubblesBackground.updateWithView(senderInfoView)
    }

    private func setupLayout() {
        addSubview(bubblesBackground)
        addSubview(welcomeMessageLabel)
        addSubview(loginButton)

        bubblesBackground.pinToSuperviewTop()
        bubblesBackground.pinToSuperviewLeading()
        bubblesBackground.pinToSuperviewTrailing()
        bubblesBackground.pinToSuperviewBottom()

        welcomeMessageLabel.alignVerticalCenterWithSuperview()
        welcomeMessageLabel.pinToSuperviewLeading(withConstant: 40)
        welcomeMessageLabel.pinToSuperviewTrailing(withConstant: 40)

        loginButton.attachToBottomOf(welcomeMessageLabel, withConstant: 35)
        loginButton.pinToSuperviewLeading(withConstant: 75)
        loginButton.pinToSuperviewTrailing(withConstant: 75)
        loginButton.addHeightConstraint(withConstant: 48)
    }

    private func setupActions() {
        loginButton.rx_tap.subscribe(
            onNext: { [weak self] in
                self?.welcomeDone()
            }).addDisposableTo(actionDisposeBag)
    }

    private func welcomeDone() {
        actionListener?.welcomeDone()
    }
}

extension WelcomeView: WelcomeDisplayer {
    func display(user: User) {
        setUserPhoto(user.photoURL!)
        senderInfoView.updateWithUserName(user.name)
    }


    private func setUserPhoto(url: NSURL) {
        imageRequestDisposeBag = DisposeBag()

        imageForURL(url)
            .observeOn(MainScheduler.instance)
            .subscribeNext({ [weak self] image in
                self?.senderInfoView.updateWithProfileImage(image!)
                }).addDisposableTo(imageRequestDisposeBag)
    }

    //TODO: Pull into utilities
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


