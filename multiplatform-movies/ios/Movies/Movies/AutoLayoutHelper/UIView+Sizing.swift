import UIKit

extension UIView {
    @discardableResult public func setWidthConstraint(
        equalTo view: UIView? = nil,
        constant: CGFloat = 0,
        multiplier: CGFloat = 1,
        priority: UILayoutPriority = .required,
        relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        
        translatesAutoresizingMaskIntoConstraints = false
        
        let constraint = NSLayoutConstraint(item: self,
                                            attribute: .width,
                                            relatedBy: relation,
                                            toItem: view,
                                            attribute: .width,
                                            multiplier: multiplier,
                                            constant: constant)
        constraint.priority = priority
        constraint.isActive = true
        return constraint
    }
    
    @discardableResult public func setHeightConstraint(
        equalTo view: UIView? = nil,
        attribute: NSLayoutAttribute = .height,
        constant: CGFloat = 0,
        multiplier: CGFloat = 1,
        priority: UILayoutPriority = .required,
        relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        
        translatesAutoresizingMaskIntoConstraints = false
        
        let constraint = NSLayoutConstraint(item: self,
                                            attribute: .height,
                                            relatedBy: relation,
                                            toItem: view,
                                            attribute: attribute,
                                            multiplier: multiplier,
                                            constant: constant)
        constraint.priority = priority
        constraint.isActive = true
        return constraint
    }
    
    public func setSizeConstraint(size: CGSize,
                                  equalTo view: UIView? = nil,
                                  constant: CGFloat = 0,
                                  priority: UILayoutPriority = .required,
                                  relatedBy relation: NSLayoutRelation = .equal) {
        setWidthConstraint(equalTo: view, constant: size.width, priority: priority, relatedBy: relation)
        setHeightConstraint(equalTo: view, constant: size.height, priority: priority, relatedBy: relation)
    }
}

