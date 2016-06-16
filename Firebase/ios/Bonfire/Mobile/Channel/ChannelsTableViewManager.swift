import UIKit

protocol ChannelsCollectionViewActionListener: class {
    func didSelectChannel(channel: Channel)
}

final class ChannelsCollectionViewManager: NSObject, UICollectionViewDataSource, UICollectionViewDelegate {

    private var channels = Channels()
    weak var actionListener: ChannelsCollectionViewActionListener?

    func updateCollectionView(collectionView: UICollectionView, withChannels channels: Channels) {
        self.channels = channels
        collectionView.reloadData()
    }

    func setupCollectionView(collectionView: UICollectionView) {
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(ChannelCell)
    }

    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return channels.count
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell: ChannelCell = collectionView.dequeueReusableCell(forIndexPath: indexPath)

        let channel = channels.channels[indexPath.row]
        cell.updateWithChannel(channel)

        return cell
    }

    func collectionView(collectionView: UICollectionView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let channel = channels.channels[indexPath.row]
        actionListener?.didSelectChannel(channel)
    }

    func collectionView(collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                               sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize {
        return CGSize(width: 70, height: 70)
    }

}
