import UIKit

final class ChatViewController: UIViewController {
    let chatView: ChatView
    let chatPresenter: ChatPresenter

    var bottomConstraint: NSLayoutConstraint!

    static func withDependencies(channel channel: Channel) -> ChatViewController {
        let chatView = ChatView()
        let presenter = ChatPresenter(
            channel: channel,
            loginService: SharedServices.loginService,
            chatService: SharedServices.chatService,
            chatDisplayer: chatView,
            navigator: SharedServices.navigator,
            analytics: SharedServices.analytics
        )

        return ChatViewController(presenter: presenter, view: chatView)
    }

    init(presenter: ChatPresenter, view: ChatView) {
        self.chatPresenter = presenter
        self.chatView = view

        super.init(nibName: nil, bundle: nil)

        self.chatView.navigationItemDelegate = self
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
        automaticallyAdjustsScrollViewInsets = true

        view.addSubview(chatView)
        chatView.pinToSuperviewTop()
        chatView.pinToSuperviewLeading()
        chatView.pinToSuperviewTrailing()

        bottomConstraint = chatView.pinToSuperviewBottom()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)

        setupKeyboardNotifcationListener()
        chatPresenter.startPresenting()
        styleNavigationBar()
    }

    override func viewDidDisappear(animated: Bool) {
        chatPresenter.stopPresenting()
        removeKeyboardNotificationListeners()
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

extension ChatViewController: ChatViewNavigationItemDelegate {
    func updateNavigationItem(withChat chat: Chat) {
        chatView.updateNavigationItem(navigationItem, chat: chat)
    }
}

// MARK: - Keyboard Handling
extension ChatViewController {
    func setupKeyboardNotifcationListener() {
        let notificationCenter = NSNotificationCenter.defaultCenter()
        notificationCenter.addObserver(self,
                                       selector: #selector(keyboardWillShow(_:)),
                                       name: UIKeyboardWillShowNotification,
                                       object: nil)
        notificationCenter.addObserver(self,
                                       selector: #selector(keyboardWillHide(_:)),
                                       name: UIKeyboardWillHideNotification,
                                       object: nil)
    }

    func removeKeyboardNotificationListeners() {
        let notificationCenter = NSNotificationCenter.defaultCenter()
        notificationCenter.removeObserver(self, name: UIKeyboardWillShowNotification, object: nil)
        notificationCenter.removeObserver(self, name: UIKeyboardWillHideNotification, object: nil)
    }

    func keyboardWillShow(notification: NSNotification) {
        guard let userInfo = notification.userInfo as? Dictionary<String, AnyObject>,
            let animationDuration = userInfo[UIKeyboardAnimationDurationUserInfoKey] as? NSTimeInterval,
            let animationCurve = userInfo[UIKeyboardAnimationCurveUserInfoKey]?.intValue,
            let keyboardFrame = userInfo[UIKeyboardFrameEndUserInfoKey]?.CGRectValue
            else { return }

        let keyboardFrameConvertedToViewFrame = view.convertRect(keyboardFrame, fromView: nil)
        let curveAnimationOption = UIViewAnimationOptions(rawValue: UInt(animationCurve))
        let options = UIViewAnimationOptions.BeginFromCurrentState.union(curveAnimationOption)

        UIView.animateWithDuration(animationDuration, delay: 0, options:options, animations: { () -> Void in
            self.bottomConstraint.constant = keyboardFrameConvertedToViewFrame.height
            self.view.layoutIfNeeded()
            }, completion: nil)
    }

    func keyboardWillHide(notification: NSNotification) {
        guard let userInfo = notification.userInfo as? Dictionary<String, AnyObject>,
            let animationDuration = userInfo[UIKeyboardAnimationDurationUserInfoKey] as? NSTimeInterval,
            let animationCurve = userInfo[UIKeyboardAnimationCurveUserInfoKey]?.intValue
            else { return }

        let curveAnimationOption = UIViewAnimationOptions(rawValue: UInt(animationCurve))
        let options = UIViewAnimationOptions.BeginFromCurrentState.union(curveAnimationOption)

        UIView.animateWithDuration(animationDuration, delay: 0, options:options, animations: { () -> Void in
            self.bottomConstraint.constant = 0
            self.view.layoutIfNeeded()
            }, completion: nil)
    }
}
