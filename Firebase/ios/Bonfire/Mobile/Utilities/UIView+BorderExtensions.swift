import UIKit


extension UIView {

    func addBottomBorder(withColor color: UIColor, height: CGFloat) {
        let border = UIView()
        border.backgroundColor = color
        addSubview(border)
        border.pinToSuperviewBottom()
        border.pinToSuperviewLeading()
        border.pinToSuperviewTrailing()
        border.addHeightConstraint(withConstant: height)
    }

}
