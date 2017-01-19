import UIKit

final class ChatViewController: UIViewController {
    let chatView = ChatView()
    let chatPresenter: ChatPresenter

    var bottomConstraint: NSLayoutConstraint!

    static func withDependencies() -> ChatViewController {
        return ChatViewController(loginService: SharedServices.loginService, chatService: SharedServices.chatService)
    }

    init(loginService: LoginService, chatService: ChatService) {
        self.chatPresenter = ChatPresenter(loginService: loginService, chatService: chatService, chatDisplayer: chatView)

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
        title = "Bonfire"
    }

    private func setupLayout() {
        automaticallyAdjustsScrollViewInsets = false

        view.addSubview(chatView)
        chatView.pinToTopLayoutGuide(viewController: self)
        chatView.pinToSuperviewLeading()
        chatView.pinToSuperviewTrailing()

        bottomConstraint = chatView.pinToSuperviewBottom()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        setupKeyboardNotifcationListener()
        chatPresenter.startPresenting()
    }

    override func viewDidDisappear(animated: Bool) {
        chatPresenter.stopPresenting()
        removeKeyboardNotificationListeners()
        super.viewDidDisappear(animated)
    }
}

// MARK: - Keyboard Handling
extension ChatViewController {
    func setupKeyboardNotifcationListener() {
        NSNotificationCenter.defaultCenter().addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIKeyboardWillShowNotification, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIKeyboardWillHideNotification, object: nil)
    }

    func removeKeyboardNotificationListeners() {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: UIKeyboardWillShowNotification, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: UIKeyboardWillHideNotification, object: nil)
    }

    func keyboardWillShow(notification: NSNotification) {
        let userInfo = notification.userInfo as! Dictionary<String, AnyObject>
        let animationDuration = userInfo[UIKeyboardAnimationDurationUserInfoKey] as! NSTimeInterval
        let animationCurve = userInfo[UIKeyboardAnimationCurveUserInfoKey]!.intValue
        let keyboardFrame = userInfo[UIKeyboardFrameEndUserInfoKey]?.CGRectValue
        let keyboardFrameConvertedToViewFrame = view.convertRect(keyboardFrame!, fromView: nil)
        let curveAnimationOption = UIViewAnimationOptions(rawValue: UInt(animationCurve))
        let options = UIViewAnimationOptions.BeginFromCurrentState.union(curveAnimationOption)

        UIView.animateWithDuration(animationDuration, delay: 0, options:options, animations: { () -> Void in
            self.bottomConstraint.constant = keyboardFrameConvertedToViewFrame.height
            self.view.layoutIfNeeded()
        }, completion: nil)
    }

    func keyboardWillHide(notification: NSNotification) {
        let userInfo = notification.userInfo as! Dictionary<String, AnyObject>
        let animationDuration = userInfo[UIKeyboardAnimationDurationUserInfoKey] as! NSTimeInterval
        let animationCurve = userInfo[UIKeyboardAnimationCurveUserInfoKey]!.intValue
        let curveAnimationOption = UIViewAnimationOptions(rawValue: UInt(animationCurve))
        let options = UIViewAnimationOptions.BeginFromCurrentState.union(curveAnimationOption)

        UIView.animateWithDuration(animationDuration, delay: 0, options:options, animations: { () -> Void in
            self.bottomConstraint.constant = 0
            self.view.layoutIfNeeded()
        }, completion: nil)
    }
}
