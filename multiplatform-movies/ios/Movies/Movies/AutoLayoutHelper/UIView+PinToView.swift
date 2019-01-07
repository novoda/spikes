import UIKit

extension UIView {
    @discardableResult public func pinTop(to view: UIView,
                                          constant: CGFloat = 0,
                                          priority: UILayoutPriority = .required,
                                          relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pin(edge: .top, to: .top, of: view, constant: constant, priority: priority, relatedBy: relation)
    }
    
    @discardableResult public func pinBottom(to view: UIView,
                                             constant: CGFloat = 0,
                                             priority: UILayoutPriority = .required,
                                             relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pin(edge: .bottom, to: .bottom, of: view, constant: constant, priority: priority, relatedBy: relation)
    }
    
    @discardableResult public func pinLeading(to view: UIView,
                                              constant: CGFloat = 0,
                                              priority: UILayoutPriority = .required,
                                              relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pin(edge: .leading, to: .leading, of: view, constant: constant, priority: priority, relatedBy: relation)
    }
    
    @discardableResult public func pinTrailing(to view: UIView,
                                               constant: CGFloat = 0,
                                               priority: UILayoutPriority = .required,
                                               relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        return pin(edge: .trailing, to: .trailing, of: view, constant: constant, priority: priority, relatedBy: relation)
    }
    
    @discardableResult public func pinView(to view: UIView,
                                           constant: CGFloat = 0,
                                           priority: UILayoutPriority = .required,
                                           edge: NSLayoutAttribute,
                                           multiplier: CGFloat = 1,
                                           relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        
        translatesAutoresizingMaskIntoConstraints = false
        
        //TODO: This assumption is not true, we should iterate superviews until we find the first common ancestor
        if view !== self.superview {
            view.translatesAutoresizingMaskIntoConstraints = false
        }
        
        let constraint = NSLayoutConstraint(item: self,
                                            attribute: edge,
                                            relatedBy: relation,
                                            toItem: view,
                                            attribute: edge,
                                            multiplier: multiplier,
                                            constant: constant)
        constraint.priority = priority
        constraint.isActive = true
        
        return constraint
    }
    
    @discardableResult public func pin(edge: Edge,
                                       to otherEdge: Edge,
                                       of view: UIView,
                                       constant: CGFloat = 0,
                                       priority: UILayoutPriority = .required,
                                       relatedBy relation: NSLayoutRelation = .equal) -> NSLayoutConstraint {
        guard let superview = self.superview else {
            preconditionFailure("view has no superview")
        }
        
        translatesAutoresizingMaskIntoConstraints = false
        if view !== superview {
            view.translatesAutoresizingMaskIntoConstraints = false
        }
        
        let constraint = NSLayoutConstraint(item: self,
                                            attribute: edge.layoutAttribute(),
                                            relatedBy: relation,
                                            toItem: view,
                                            attribute: otherEdge.layoutAttribute(),
                                            multiplier: 1,
                                            constant: constant)
        constraint.priority = priority
        superview.addConstraint(constraint)
        return constraint
    }
}
