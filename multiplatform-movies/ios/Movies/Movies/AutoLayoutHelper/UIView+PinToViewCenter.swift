import UIKit

extension UIView {
    @discardableResult public func pinCenterX(to view: UIView,
                                              constant: CGFloat = 0,
                                              priority: UILayoutPriority = .required,
                                              relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pinView(to: view, constant: constant, priority: priority, edge: .centerX, relatedBy: relation)
    }
    
    @discardableResult public func pinCenterY(to view: UIView,
                                              constant: CGFloat = 0,
                                              priority: UILayoutPriority = .required,
                                              multiplier: CGFloat = 1,
                                              relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pinView(to: view, constant: constant, priority: priority,
                       edge: .centerY,
                       multiplier: multiplier,
                       relatedBy: relation)
    }
    
    public func pinCenter(to view: UIView,
                          constant: CGFloat = 0,
                          priority: UILayoutPriority = .required,
                          relatedBy relation: NSLayoutRelation = .equal) {
        
        pinCenterY(to: view, constant: constant, priority: priority, relatedBy: relation)
        pinCenterX(to: view, constant: constant, priority: priority, relatedBy: relation)
    }
}

