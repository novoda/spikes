import UIKit
import Firebase
import FirebaseAuth

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
        let shareBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Action, target: self, action: #selector(shareBonfire))
        navigationItem.setRightBarButtonItem(newChannelBarButtonItem, animated: false)
        navigationItem.setLeftBarButtonItem(shareBarButtonItem, animated: false)
    }

    func shareBonfire() {
        guard let currentUserName = FIRAuth.auth()?.currentUser?.displayName
            else { return }

        let deeplinkURL = NSURLComponents(string: "https://bonfire.com/welcome")!
        deeplinkURL.queryItems = [
            NSURLQueryItem(name: "sender", value: currentUserName)
        ]

        print(deeplinkURL.string)

        let shareURL = NSURLComponents(string: "https://t6c2e.app.goo.gl")!
        shareURL.queryItems = [
            NSURLQueryItem(name: "link", value: deeplinkURL.string),
            NSURLQueryItem(name: "ibi", value: "com.novoda.bonfire")
        ]

        print(shareURL.string)

        let message = "Check out Bonfire!"
        let activityController = UIActivityViewController(activityItems: [message, shareURL.URL!], applicationActivities: nil)
        self.presentViewController(activityController, animated: true, completion: nil)
    }

}
