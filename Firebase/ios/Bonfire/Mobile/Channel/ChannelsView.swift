import UIKit

final class ChannelsView: UIView {
    private let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: UICollectionViewFlowLayout())
    private let collectionViewManager = ChannelsCollectionViewManager()
    private weak var actionListener: ChannelsActionListener?

    let newChannelBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Compose, target: nil, action: nil)
    let shareBonfireBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Action, target: nil, action: nil)

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
        setupLayout()
        newChannelBarButtonItem.target = self
        newChannelBarButtonItem.action = #selector(newChannel)
        shareBonfireBarButtonItem.target = self
        shareBonfireBarButtonItem.action = #selector(shareBonfire)
    }

    convenience init() {
        self.init(frame: CGRect.zero)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupViews() {
        collectionViewManager.setupCollectionView(collectionView)
        collectionViewManager.actionListener = self

        if let flowLayout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
            flowLayout.sectionInset = UIEdgeInsets(top: 15, left: 15, bottom: 15, right: 15)
        }

        collectionView.backgroundColor = UIColor(red: 244/255.0, green: 244/255.0, blue: 244/255.0, alpha: 1.0)
    }

    private func setupLayout() {
        addSubview(collectionView)

        collectionView.pinToSuperviewTop()
        collectionView.pinToSuperviewBottom()

        collectionView.pinToSuperviewLeading()
        collectionView.pinToSuperviewTrailing()
    }

    @objc private func newChannel() {
        actionListener?.goToNewChannel()
    }

    @objc private func shareBonfire() {
        actionListener?.shareBonfire()
    }
}

extension ChannelsView: ChannelsDisplayer {
    func display(channels: Channels) {
        collectionViewManager.updateCollectionView(collectionView, withChannels: channels)
    }

    func attach(actionListener: ChannelsActionListener) {
        self.actionListener = actionListener
    }

    func detach(actionListener: ChannelsActionListener) {
        self.actionListener = nil
    }
}

extension ChannelsView: ChannelsCollectionViewActionListener {
    func didSelectChannel(channel: Channel) {
        actionListener?.viewChannel(channel)
    }
}
