import UIKit

final class ChannelsViewController: UIViewController {
    let channelsView = ChannelsView()
    let channelsPresenter: ChannelsPresenter

    static func withDependencies() -> ChannelsViewController {
        return ChannelsViewController(loginService: SharedServices.loginService, channelsService: SharedServices.channelsService, navigator: SharedServices.navigator, config: SharedServices.config)
    }

    init(loginService: LoginService, channelsService: ChannelsService, navigator: Navigator, config: Config) {
        self.channelsPresenter = ChannelsPresenter(loginService: loginService, channelsService: channelsService, channelsDisplayer: channelsView, navigator: navigator, config: config)

        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
        title = "Channels"
        addBarButtonItem()
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
    }

    override func viewDidDisappear(animated: Bool) {
        channelsPresenter.stopPresenting()
        super.viewDidDisappear(animated)
    }

    func addBarButtonItem() {
        let newChannelBarButtonItem = self.channelsView.newChannelBarButtonItem
        navigationItem.rightBarButtonItem = newChannelBarButtonItem
        navigationItem.setRightBarButtonItem(newChannelBarButtonItem, animated: false)

    }

}
