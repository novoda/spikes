//
//  ViewController.swift
//  Movies
//
//  Created by Tobias Heine on 09.01.19.
//  Copyright © 2019 Novoda. All rights reserved.
//

import UIKit
import SDWebImage
import main

class GalleryViewController: UICollectionViewController, GalleryPresenterView {
    
    private let presenter = GalleryDependencyProvider(networkingDependencyProvider: NetworkingDependencyProvider()).providerPresenter()
    private let cellIdentifier = "movie_poster"
    
    private var movies : [MoviePoster] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        collectionView?.backgroundColor = .white
        collectionView?.register(MoviePosterCell.self, forCellWithReuseIdentifier: cellIdentifier)
        presenter.startPresenting(view: self)
    }
    
    func render(gallery: Gallery) {
        movies = gallery.moviePosters
        collectionView?.reloadData()
    }
    
    func renderError(message: String?) {
        print(message)
//        guard let errorMessage = message else {
//            label.text = "Something went wrong"
//            return
//        }
//        label.text = errorMessage
    }
}
//MARK: - UICollectionViewDatasource
extension GalleryViewController {
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
            return movies.count
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellIdentifier, for: indexPath) as! MoviePosterCell
        let movie = movies[indexPath.row]
        cell.posterImage.sd_setImage(with: URL(string: movie.thumbnailUrl), placeholderImage: nil)
        cell.backgroundColor = .blue
        return cell
    }
    
}

extension GalleryViewController: UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: collectionView.frame.width/2, height: 310)
    }
}

class MoviePosterCell: UICollectionViewCell {
    let posterImage = UIImageView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        posterImage.contentMode = .scaleAspectFit
        posterImage.clipsToBounds = true
        posterImage.isUserInteractionEnabled = false
        
        contentView.addSubview(posterImage)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        posterImage.frame = contentView.frame
    }
}
