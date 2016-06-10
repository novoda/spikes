import UIKit

class UsersViewController: UIViewController {

    let usersView: UsersView
    let usersPresenter: UsersPresenter

    var bottomConstraint: NSLayoutConstraint!


    static func withDependencies(channel channel: Channel) -> UsersViewController {
        let usersView = UsersView()
        let usersPresenter = UsersPresenter(
            channel: channel,
            usersService: SharedServices.usersService,
            channelsService: SharedServices.channelsService,
            usersDisplayer: usersView,
            navigator: SharedServices.navigator
        )

        return UsersViewController(presenter: usersPresenter, view: usersView)
    }

    init(presenter: UsersPresenter, view: UsersView) {
        self.usersPresenter = presenter
        self.usersView = view

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        self.view = UIView()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(usersView)
        usersView.pinToSuperviewTop()
        usersView.pinToSuperviewLeading()
        usersView.pinToSuperviewTrailing()

        bottomConstraint = usersView.pinToSuperviewBottom()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        usersPresenter.startPresenting()

        styleNavigationBar()
    }

    override func viewDidDisappear(animated: Bool) {
        usersPresenter.stopPresenting()
        super.viewDidDisappear(animated)
    }


    func styleNavigationBar() {
        navigationController?.navigationBar.barTintColor = nil
        navigationController?.navigationBar.tintColor = BonfireColors.orange
        navigationController?.navigationBar.titleTextAttributes = nil
        navigationController?.navigationBar.barStyle = .Default
        navigationController?.view.backgroundColor = UIColor.clearColor()

        UIApplication.sharedApplication().statusBarStyle = .Default
    }
}
