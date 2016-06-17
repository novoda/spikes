import UIKit

class BubbleBackgroundView: UIView {
    var bigBubble = UIView()
    var mediumBubble = UIView()
    var smallBubble = UIView()

    var containerView = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupHierarchy()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupHierarchy()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        setupBigBubble()
        setupMediumBubble(withYAnchor: bigBubble.frame.maxY)
        setupSmallBubble(withYAnchor: mediumBubble.frame.maxY)
        setupContainerView()
    }

    func updateWithView(view: UIView) {
        containerView = view
        addSubview(containerView)
    }

    private func setupHierarchy() {
        addSubview(bigBubble)
        addSubview(mediumBubble)
        addSubview(smallBubble)

        bigBubble.backgroundColor = .whiteColor()
        mediumBubble.backgroundColor = .whiteColor()
        smallBubble.backgroundColor = .whiteColor()
    }

    private func setupBigBubble() {
        let bigBubbleDimensions: CGFloat = frame.width * 3

        let y = 0 - bigBubbleDimensions * 0.8

        bigBubble.frame = CGRect(x: frame.midX - (bigBubbleDimensions / 2), y: y, width: bigBubbleDimensions, height: bigBubbleDimensions)

        bigBubble.layer.cornerRadius = bigBubbleDimensions / 2
    }

    private func setupMediumBubble(withYAnchor yAnchor: CGFloat) {
        let mediumBubbleDimensions: CGFloat = frame.width / 18
        mediumBubble.frame = CGRect(x: frame.midX - (mediumBubbleDimensions / 2), y: yAnchor - (mediumBubbleDimensions / 2), width: mediumBubbleDimensions, height: mediumBubbleDimensions)
        mediumBubble.layer.cornerRadius = mediumBubbleDimensions / 2
    }

    private func setupSmallBubble(withYAnchor yAnchor: CGFloat) {
        let smallBubbleDimensions: CGFloat = frame.width / 36
        smallBubble.frame = CGRect(x: frame.midX - (smallBubbleDimensions / 2), y: yAnchor + smallBubbleDimensions, width: smallBubbleDimensions, height: smallBubbleDimensions)
        smallBubble.layer.cornerRadius = smallBubbleDimensions / 2
    }

    private func setupContainerView() {
        let visibleBigBubbleFrame = CGRectIntersection(bigBubble.frame, self.frame)

        containerView.addEqualWidthConstraint(withView: self, withMultiplier: 0.6)
        containerView.addAspectRatioConstraint(withMultiplier: 1.0)
        containerView.center = CGPointMake(visibleBigBubbleFrame.midX, visibleBigBubbleFrame.midY * 0.9)
    }

}
