import UIKit

// MARK: - Pin to Superview Edges

extension UIView {

    public func pinToLayoutGuides(
        viewController viewController: UIViewController,
                       withInsets insets: UIEdgeInsets = UIEdgeInsetsZero) {

        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }

        self.translatesAutoresizingMaskIntoConstraints = false

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Top, relatedBy: .Equal,
            toItem: viewController.topLayoutGuide, attribute: .Top, multiplier: 1.0, constant: insets.top))

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Bottom, relatedBy: .Equal,
            toItem: viewController.bottomLayoutGuide, attribute: .Bottom, multiplier: 1.0, constant: insets.bottom))

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Left, relatedBy: .Equal,
            toItem: superview, attribute: .Left, multiplier: 1.0, constant: insets.left))

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Right, relatedBy: .Equal,
            toItem: superview, attribute: .Right, multiplier: 1.0, constant: insets.right))
    }

    public func pinToTopLayoutGuide(
        viewController viewController: UIViewController,
                       constant: CGFloat = 0) -> NSLayoutConstraint {
        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }

        self.translatesAutoresizingMaskIntoConstraints = false

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Top,
            relatedBy: .Equal,
            toItem: viewController.topLayoutGuide, attribute: .Bottom,
            multiplier: 1.0, constant: constant
        )

        superview.addConstraint(constraint)

        return constraint
    }

    public func pinToSuperviewEdges(withInsets insets: UIEdgeInsets = UIEdgeInsetsZero) {
        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }

        self.translatesAutoresizingMaskIntoConstraints = false

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Top, relatedBy: .Equal,
            toItem: superview, attribute: .Top, multiplier: 1.0, constant: insets.top))

        superview.addConstraint(NSLayoutConstraint(item: self, attribute: .Left, relatedBy: .Equal,
            toItem: superview, attribute: .Left, multiplier: 1.0, constant: insets.left))

        superview.addConstraint(NSLayoutConstraint(item: superview, attribute: .Bottom, relatedBy: .Equal,
            toItem: self, attribute: .Bottom, multiplier: 1.0, constant: insets.bottom))

        superview.addConstraint(NSLayoutConstraint(item: superview, attribute: .Right, relatedBy: .Equal,
            toItem: self, attribute: .Right, multiplier: 1.0, constant: insets.right))
    }

    public func pinToSuperviewLeading(
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {
        let superview = unWrappedSuperview()

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Leading,
            relatedBy: .Equal,
            toItem: superview, attribute: .Leading,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority
        superview.addConstraint(constraint)

        return constraint
    }

    public func pinToSuperviewTrailing(
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {
        let superview = unWrappedSuperview()

        let constraint = NSLayoutConstraint(
            item: superview, attribute: .Trailing,
            relatedBy: .Equal,
            toItem: self, attribute: .Trailing,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority
        superview.addConstraint(constraint)

        return constraint
    }

    public func pinToSuperviewTop(
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        let superview = unWrappedSuperview()

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Top,
            relatedBy: .Equal,
            toItem: superview, attribute: .Top,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority
        superview.addConstraint(constraint)

        return constraint
    }

    public func pinToSuperviewBottom(
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        let superview = unWrappedSuperview()

        let constraint = NSLayoutConstraint(
            item: superview, attribute: .Bottom,
            relatedBy: .Equal,
            toItem: self, attribute: .Bottom,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority
        superview.addConstraint(constraint)

        return constraint
    }

    private func unWrappedSuperview() -> UIView {
        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }

        self.translatesAutoresizingMaskIntoConstraints = false

        return superview
    }
}

// MARK: - Self Constraint

extension UIView {

    public func addWidthConstraint(
        withConstant constant: CGFloat,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        translatesAutoresizingMaskIntoConstraints = false

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Width,
            relatedBy: .Equal,
            toItem: nil, attribute: .NotAnAttribute,
            multiplier: 1, constant: constant
        )

        constraint.priority = priority
        addConstraint(constraint)
        return constraint
    }

    public func addHeightConstraint(
        withConstant constant: CGFloat,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        translatesAutoresizingMaskIntoConstraints = false

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Height,
            relatedBy: .Equal,
            toItem: nil, attribute: .NotAnAttribute,
            multiplier: 1, constant: constant
        )

        constraint.priority = priority
        addConstraint(constraint)
        return constraint
    }

    public func addAspectRatioConstraint(
        withMultiplier multiplier: CGFloat,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        translatesAutoresizingMaskIntoConstraints = false

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Height,
            relatedBy: .Equal,
            toItem: self, attribute: .Width,
            multiplier: multiplier, constant: 0.0
        )

        constraint.priority = priority
        addConstraint(constraint)
        return constraint
    }

}

// MARK: - Attach to Sibling

extension UIView {
    public func attachToBottomOf(
        view: UIView,
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Top,
            relatedBy: .Equal,
            toItem: view, attribute: .Bottom,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority

        addSiblingConstraint(constraint)

        return constraint
    }

    public func attachToRightOf(
        view: UIView,
        withConstant constant: CGFloat = 0,
                     priority: UILayoutPriority = UILayoutPriorityRequired
        ) -> NSLayoutConstraint {

        let constraint = NSLayoutConstraint(
            item: self, attribute: .Left,
            relatedBy: .Equal,
            toItem: view, attribute: .Right,
            multiplier: 1.0, constant: constant
        )

        constraint.priority = priority

        addSiblingConstraint(constraint)

        return constraint
    }

    private func addSiblingConstraint(constraint: NSLayoutConstraint) {
        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }

        if let firstItem = constraint.firstItem as? UIView where firstItem != superview {
            firstItem.translatesAutoresizingMaskIntoConstraints = false
        }

        if let secondItem = constraint.secondItem as? UIView where secondItem != superview {
            secondItem.translatesAutoresizingMaskIntoConstraints = false
        }

        superview.addConstraint(constraint)
    }
}


// MARK: - Align With Siblings

extension UIView {
    public func alignHorizontalCenter(withView view: UIView, withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .CenterX,
            relatedBy: .Equal,
            toItem: view, attribute: .CenterX,
            multiplier: multiplier, constant: 0
        )

        addSiblingConstraint(constraint)

        return constraint
    }

    public func alignHorizontalCenterWithSuperview(withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let superview = unWrappedSuperview()
        return alignHorizontalCenter(withView: superview, withMultiplier: multiplier)
    }


    public func alignVerticalCenter(withView view: UIView, withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .CenterY,
            relatedBy: .Equal,
            toItem: view, attribute: .CenterY,
            multiplier: multiplier, constant: 0
        )

        addSiblingConstraint(constraint)

        return constraint
    }

    public func alignVerticalCenterWithSuperview(withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let superview = unWrappedSuperview()
        return alignVerticalCenter(withView: superview, withMultiplier: multiplier)
    }

    public func alignLeading(withView view: UIView, withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .Leading,
            relatedBy: .Equal,
            toItem: view, attribute: .Leading,
            multiplier: multiplier, constant: 0
        )
        addSiblingConstraint(constraint)

        return constraint
    }


    public func alignTrailing(withView view: UIView) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .Trailing,
            relatedBy: .Equal,
            toItem: view, attribute: .Trailing,
            multiplier: 1.0, constant: 0
        )

        addSiblingConstraint(constraint)

        return constraint
    }
}

// MARK: Equal Dimensions

extension UIView {

    public func addEqualWidthConstraint(withView view: UIView, withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .Width,
            relatedBy: .Equal,
            toItem: view, attribute: .Width,
            multiplier: multiplier, constant: 0
        )

        addSiblingConstraint(constraint)

        return constraint
    }

    public func addEqualHeightConstraint(withView view: UIView, withMultiplier multiplier: CGFloat = 1.0) -> NSLayoutConstraint {
        let constraint = NSLayoutConstraint(
            item: self, attribute: .Height,
            relatedBy: .Equal,
            toItem: view, attribute: .Height,
            multiplier: multiplier, constant: 0
        )

        addSiblingConstraint(constraint)

        return constraint
    }

}
