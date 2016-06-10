import UIKit

class WelcomeViewController: UIViewController, WelcomeActionListener {

    let welcomeView: WelcomeView
    let navigator: Navigator

    var bottomConstraint: NSLayoutConstraint!

    static func withDependencies(sender sender: String?) -> WelcomeViewController {
        let welcomeView = WelcomeView(sender: sender)
        let navigator = SharedServices.navigator
        return WelcomeViewController(view: welcomeView, navigator: navigator)
    }

    init(view: WelcomeView, navigator: Navigator) {
        self.welcomeView = view
        self.navigator = navigator

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        self.view = UIView()

        navigationController?.navigationBarHidden = true
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLayout()
    }

    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(true)
        welcomeView.actionListener = self
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(welcomeView)
        welcomeView.pinToTopLayoutGuide(viewController: self)
        welcomeView.pinToSuperviewLeading()
        welcomeView.pinToSuperviewTrailing()

        bottomConstraint = welcomeView.pinToSuperviewBottom()
    }

    func welcomeDone() {
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
}