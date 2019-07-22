//
// Created by Alex Curran on 03.09.18.
// Copyright (c) 2018 Oetker Digital GmbH. All rights reserved.
//

import UIKit
import TinyConstraints

@available(iOS 10, *)
public protocol DrawerViewDelegate: class {
  func filterViewDidCollapse(_ filterView: DrawerView)
  func filterViewWillExpand(_ filterView: DrawerView)
}

@available(iOS 10, *)
// swiftlint:disable:next type_body_length we will revisit the internals of this class when adding more functionality
open class DrawerView: UIView {
  public let peekView: UIView
  private let contentView: UIView
  private let scrollingContentView = UIScrollView(frame: .zero)

  private lazy var panToExpandGestureRecogniser = UIPanGestureRecognizer(target: self, action: #selector(collapsedFilterDidPan))
  private lazy var panToCollapseGestureRecogniser = UIPanGestureRecognizer(target: self, action: #selector(expandedFilterDidPan))
  private var initialTouchPosition: CGFloat = 0
  // swiftlint:disable:next implicitly_unwrapped_optional we always create it before using
  private var propertyAnimator: UIViewPropertyAnimator!
  // swiftlint:disable:next implicitly_unwrapped_optional the constraint is mandatory for this class
  public var animatingConstraint: NSLayoutConstraint!
  public weak var delegate: DrawerViewDelegate?

  public init(peekView: UIView, contentView: UIView) {
    self.peekView = peekView
    self.contentView = contentView
    super.init(frame: .zero)

    addSubview(peekView)
    setupPeekView()

    addSubview(scrollingContentView)

    scrollingContentView.backgroundColor = .white
    scrollingContentView.topToBottom(of: peekView)
    scrollingContentView.leftToSuperview()
    scrollingContentView.rightToSuperview()
    scrollingContentView.bottomToSuperview()

    scrollingContentView.addSubview(contentView)
    contentView.edgesToSuperview()
    contentView.width(to: self)
  }

  private func setupPeekView() {
    peekView.edgesToSuperview(excluding: .bottom)

    let tapGestureRecogniser = UITapGestureRecognizer(target: self, action: #selector(filterDidTap))
    peekView.addGestureRecognizer(tapGestureRecogniser)
    peekView.addGestureRecognizer(panToExpandGestureRecogniser)
    peekView.addGestureRecognizer(panToCollapseGestureRecogniser)
    panToCollapseGestureRecogniser.isEnabled = false
    peekView.isUserInteractionEnabled = true
  }

  public required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  @objc
  func filterDidTap(_ gestureRecogniser: UITapGestureRecognizer) {
    if isExpanded {
      collapse()
    } else {
      expand()
    }
  }

  public func expand() {
    delegate?.filterViewWillExpand(self)
    animatorForExpandGesture().startAnimation()
  }

  public func collapse() {
    // this prevents us crashing under a circumstance which I can't remember
    if animatingConstraint != nil {
      animatorForCollapseGesture().startAnimation()
    }
  }

  var upperAnimationLimit: CGFloat {
    // This is a bit of a guess, could do with re-working. This prevents the filter going behind
    // the navigation bar, which means you can never close the filters.
    return -1 * min(scrollingContentView.contentSize.height, UIScreen.main.bounds.height - 300)
  }

  private var isExpanded: Bool {
    return animatingConstraint.constant == upperAnimationLimit
  }

  @objc
  func collapsedFilterDidPan(_ gestureRecogniser: UIPanGestureRecognizer) {
    switch gestureRecogniser.state {
    case .began:
      startExpandingAnimation(fromGesture: gestureRecogniser)
    case .changed:
      animateExpanding(fromGesture: gestureRecogniser)
    case .ended:
      finishExpandingAnimation(fromGesture: gestureRecogniser)
    default:
      break
    }
  }

  private func startExpandingAnimation(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }
    superView.setNeedsLayout()
    superView.layoutIfNeeded()
    initialTouchPosition = gestureRecogniser.translation(in: superView).y

    delegate?.filterViewWillExpand(self)

    propertyAnimator = animatorForExpandGesture()
    propertyAnimator.startAnimation()
    propertyAnimator.pauseAnimation()
  }

  private func animatorForExpandGesture() -> UIViewPropertyAnimator {
    let animator = UIViewPropertyAnimator(duration: .animationDuration, curve: .easeIn, animations: { [weak self] in
      guard let strongSelf = self else {
        return
      }
      strongSelf.animatingConstraint.constant = strongSelf.upperAnimationLimit
      strongSelf.superview?.layoutIfNeeded()
    })
    animator.addCompletion { [weak self] position in
      self?.settleExpandingAnimation(fromPosition: position)
    }
    return animator
  }

  private func animateExpanding(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }

    let fullLength: CGFloat = .initialOffset - upperAnimationLimit
    let currentFraction = (initialTouchPosition - gestureRecogniser.translation(in: superView).y)
      .asProportion(of: fullLength)
      .clamped(to: 0.01...0.99)
    propertyAnimator.fractionComplete = currentFraction
  }

  private func finishExpandingAnimation(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }

    let fullLength: CGFloat = .initialOffset - upperAnimationLimit
    let currentFraction = (initialTouchPosition - gestureRecogniser.translation(in: superView).y) / fullLength

    if abs(currentFraction) < .tippingPoint {
      propertyAnimator.isReversed = true
    }

    propertyAnimator.startAnimation()
  }

  private func settleExpandingAnimation(fromPosition position: UIViewAnimatingPosition) {
    if position == .start {
      // this is called when trying to drag the filters up, but letting them fall again
      delegate?.filterViewDidCollapse(self)
      animatingConstraint.constant = .initialOffset
      superview?.layoutIfNeeded()
    } else if position == .end { // filter view did expand
      panToCollapseGestureRecogniser.isEnabled = true
      panToExpandGestureRecogniser.isEnabled = false
    }
    superview?.layoutIfNeeded()
  }

  @objc
  func expandedFilterDidPan(_ gestureRecogniser: UIPanGestureRecognizer) {
    switch gestureRecogniser.state {
    case .began:
      startCollapsingAnimation(fromGesture: gestureRecogniser)
    case .changed:
      animateCollapsing(fromGesture: gestureRecogniser)
    case .ended:
      finishCollapsingAnimation(fromGesture: gestureRecogniser)
    default:
      break
    }
  }

  private func startCollapsingAnimation(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }
    superView.setNeedsLayout()
    superView.layoutIfNeeded()
    initialTouchPosition = gestureRecogniser.translation(in: superView).y

    // filter view will collapse
    propertyAnimator = animatorForCollapseGesture()
    propertyAnimator.startAnimation()
    propertyAnimator.pauseAnimation()
  }

  private func animatorForCollapseGesture() -> UIViewPropertyAnimator {
    let animator = UIViewPropertyAnimator(duration: .animationDuration, curve: .easeIn, animations: { [weak self] in
      self?.animatingConstraint.constant = .initialOffset
      self?.superview?.layoutIfNeeded()
    })
    animator.addCompletion { [weak self] position in
      self?.settleCollapsingAnimation(fromPosition: position)
    }
    return animator
  }

  private func animateCollapsing(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }

    let fullLength: CGFloat = .initialOffset - upperAnimationLimit
    let currentFraction = (gestureRecogniser.translation(in: superView).y - initialTouchPosition)
      .asProportion(of: fullLength)
      .clamped(to: 0.01...0.99)
    propertyAnimator.fractionComplete = currentFraction
  }

  private func finishCollapsingAnimation(fromGesture gestureRecogniser: UIPanGestureRecognizer) {
    guard let superView = superview else {
      return
    }

    let fullLength: CGFloat = .initialOffset - upperAnimationLimit
    let currentFraction = (initialTouchPosition - gestureRecogniser.translation(in: superView).y) / fullLength

    if abs(currentFraction) < .tippingPoint {
      propertyAnimator.isReversed = true
    }

    propertyAnimator.startAnimation()
  }

  private func settleCollapsingAnimation(fromPosition position: UIViewAnimatingPosition) {
    if position == .start {
      // this is called when trying to collapse the filters but letting them snap up again
      delegate?.filterViewWillExpand(self)
      animatingConstraint.constant = upperAnimationLimit
      superview?.layoutIfNeeded()
    } else if position == .end {
      delegate?.filterViewDidCollapse(self)
      panToExpandGestureRecogniser.isEnabled = true
      panToCollapseGestureRecogniser.isEnabled = false
    }
    superview?.layoutIfNeeded()
  }
}

private extension TimeInterval {
  static let animationDuration: TimeInterval = 0.2
}

private extension CGFloat {
    
    func asProportion(of value: CGFloat) -> CGFloat {
        return self / value
    }
    
}

private extension Comparable {
    
    func clamped(to limits: ClosedRange<Self>) -> Self {
        return min(max(self, limits.lowerBound), limits.upperBound)
    }
    
}

private extension CGFloat {
  static let initialOffset: CGFloat = 0
  static let tippingPoint: CGFloat = 0.5
} // swiftlint:disable:this file_length
