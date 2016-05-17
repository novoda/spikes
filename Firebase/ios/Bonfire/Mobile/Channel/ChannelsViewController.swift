import UIKit

final class ChannelsViewController: UIViewController {
    let channelsView = ChannelsView()
    let channelsPresenter: ChannelsPresenter

    static func withDependencies() -> ChannelsViewController {
        return ChannelsViewController(loginService: SharedServices.loginService, channelsService: SharedServices.channelsService, navigator: SharedServices.navigator)
    }

    init(loginService: LoginService, channelsService: ChannelsService, navigator: Navigator) {
        self.channelsPresenter = ChannelsPresenter(loginService: loginService, channelsService: channelsService, channelsDisplayer: channelsView, navigator: navigator)

        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
        title = "Channels"
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

}
