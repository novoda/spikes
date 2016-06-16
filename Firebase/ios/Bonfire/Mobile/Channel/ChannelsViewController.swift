import UIKit
import Firebase

final class ChannelsViewController: UIViewController {
    let channelsView = ChannelsView()
    let channelsPresenter: ChannelsPresenter

    static func withDependencies() -> ChannelsViewController {
        return ChannelsViewController(
            loginService: SharedServices.loginService,
            channelsService: SharedServices.channelsService,
            navigator: SharedServices.navigator,
            dynamicLinkFactory: SharedServices.dynamicLinkFactory,
            config: SharedServices.config
        )
    }

    init(loginService: LoginService,
         channelsService: ChannelsService,
         navigator: Navigator,
         dynamicLinkFactory: DynamicLinkFactory,
         config: Config
        ) {
        self.channelsPresenter = ChannelsPresenter(
            loginService: loginService,
            channelsService: channelsService,
            channelsDisplayer: channelsView,
            navigator: navigator,
            dynamicLinkFactory: dynamicLinkFactory,
            config: config)

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(channelsView)
        channelsView.pinToTopLayoutGuide(viewController: self)
        channelsView.pinToSuperviewLeading()
        channelsView.pinToSuperviewTrailing()
        channelsView.pinToSuperviewBottom()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)

        channelsPresenter.startPresenting()
        styleNavigationBar()
    }

    override func viewDidDisappear(animated: Bool) {
        channelsPresenter.stopPresenting()
        super.viewDidDisappear(animated)
    }

    func addBarButtonItem() {
        let newChannelBarButtonItem = channelsView.newChannelBarButtonItem
        let shareBarButtonItem = channelsView.shareBonfireBarButtonItem
        navigationItem.setRightBarButtonItem(newChannelBarButtonItem, animated: false)
        navigationItem.setLeftBarButtonItem(shareBarButtonItem, animated: false)
    }

    func styleNavigationBar() {
        navigationController?.navigationBar.barTintColor = BonfireColors.orange
        navigationController?.navigationBar.tintColor = .whiteColor()
        navigationController?.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor()]
        UIApplication.sharedApplication().statusBarStyle = .LightContent

        title = "Channels"
        addBarButtonItem()

        navigationController?.navigationBarHidden = false
    }

}
