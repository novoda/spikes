import UIKit

public extension UIView {

    @available(iOS 9.0, *)
    public func pinToSuperviewSafeArea(_ edges: Edge..., insetBy inset: CGFloat = 0) {
        prepareForConstraints()
        var edges = edges
        if edges.contains(.leading) {
            leadingAnchor.constraint(equalTo: superview!.leadingSafeAnchor,
                                     constant: offset(for: .leading, ofInset: inset)).isActive = true
            edges.remove(.leading)
        }
        if edges.contains(.trailing) {
            trailingAnchor.constraint(equalTo: superview!.trailingSafeAnchor,
                                      constant: offset(for: .trailing, ofInset: inset)).isActive = true
            edges.remove(.trailing)
        }
        if edges.contains(.top) {
            topAnchor.constraint(equalTo: superview!.topSafeAnchor,
                                 constant: offset(for: .top, ofInset: inset)).isActive = true
            edges.remove(.top)
        }
        if edges.contains(.bottom) {
            bottomAnchor.constraint(equalTo: superview!.bottomSafeAnchor,
                                    constant: offset(for: .bottom, ofInset: inset)).isActive = true
            edges.remove(.bottom)
        }
        if edges.isEmpty == false {
            debugPrint("Constraining to superview safe area was left with the following unconstrained attributes: \(edges)")
        }
    }

    private func offset(for edge: Edge, ofInset inset: CGFloat) -> CGFloat {
        switch edge {
        case .top, .leading:
            return inset
        case .bottom, .trailing:
            return -inset
        }
    }

    // This can be used in the other extensions as well
    private func prepareForConstraints() {
        guard let _ = self.superview else {
            fatalError("view doesn't have a superview")
        }
        translatesAutoresizingMaskIntoConstraints = false
    }

}

private extension Array where Element == Edge {

    mutating func remove(_ edge: Edge) {
        if let foundIndex = index(of: edge) {
            remove(at: foundIndex)
        }
    }

}
