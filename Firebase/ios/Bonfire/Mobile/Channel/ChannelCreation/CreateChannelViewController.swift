import UIKit

final class CreateChannelViewController: UIViewController {

    let createChannelView: CreateChannelView
    let createChannelPresenter: CreateChannelPresenter

    var bottomConstraint: NSLayoutConstraint!

    static func withDependencies() -> CreateChannelViewController {
        let view = CreateChannelView()
        let presenter = CreateChannelPresenter(
            loginService: SharedServices.loginService,
            channelsService: SharedServices.channelsService,
            createChannelDisplayer: view,
            navigator: SharedServices.navigator
        )

        return CreateChannelViewController(createChannelPresenter: presenter, createChannelView: view)
    }

    init(createChannelPresenter: CreateChannelPresenter, createChannelView: CreateChannelView) {
        self.createChannelPresenter = createChannelPresenter
        self.createChannelView = createChannelView
        super.init(nibName: nil, bundle:nil)
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
        title = "Create Channel"
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(createChannelView)
        createChannelView.pinToTopLayoutGuide(viewController: self)
        createChannelView.pinToSuperviewLeading()
        createChannelView.pinToSuperviewTrailing()

        bottomConstraint = createChannelView.pinToSuperviewBottom()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        createChannelPresenter.startPresenting()
    }

    override func viewDidDisappear(animated: Bool) {
        createChannelPresenter.stopPresenting()
        super.viewDidDisappear(animated)
    }

}
