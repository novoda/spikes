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
        setupKeyboardNotifcationListener()
        createChannelPresenter.startPresenting()
    }

    override func viewDidDisappear(animated: Bool) {
        createChannelPresenter.stopPresenting()
        removeKeyboardNotificationListeners()
        super.viewDidDisappear(animated)
    }

}

// MARK: - Keyboard Handling
extension CreateChannelViewController {
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
