import UIKit

class WelcomeViewController: UIViewController {
    let welcomePresenter: WelcomePresenter
    let welcomeView: WelcomeView

    var bottomConstraint: NSLayoutConstraint!

    static func withDependencies(senderID senderID: String?) -> WelcomeViewController {
        let welcomeView = WelcomeView()
        let presenter = WelcomePresenter(
            senderID: senderID!,
            userService: SharedServices.usersService,
            welcomeDisplayer: welcomeView,
            navigator: SharedServices.navigator,
            analytics: SharedServices.analytics
        )

        return WelcomeViewController(presenter: presenter, view: welcomeView)
    }

    init(presenter: WelcomePresenter, view: WelcomeView) {
        self.welcomePresenter = presenter
        self.welcomeView = view

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        self.view = UIView()

        UIApplication.sharedApplication().statusBarStyle = .Default
        navigationController?.navigationBarHidden = true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        welcomePresenter.startPresenting()
        welcomeView.actionListener = welcomePresenter
    }

    override func viewDidDisappear(animated: Bool) {
        welcomePresenter.stopPresenting()
        welcomeView.actionListener = nil
        super.viewDidDisappear(animated)
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(welcomeView)
        welcomeView.pinToTopLayoutGuide(viewController: self)
        welcomeView.pinToSuperviewLeading()
        welcomeView.pinToSuperviewTrailing()

        bottomConstraint = welcomeView.pinToSuperviewBottom()
    }

}
