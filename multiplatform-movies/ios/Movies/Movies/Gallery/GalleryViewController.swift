//
//  ViewController.swift
//  Movies
//
//  Created by Tobias Heine on 09.01.19.
//  Copyright © 2019 Novoda. All rights reserved.
//

import UIKit
import main

class GalleryViewController: UICollectionViewController, GalleryPresenterView {
    
    private let presenter = GalleryDependencyProvider(networkingDependencyProvider: NetworkingDependencyProvider()).providerPresenter()

    private var movies : [MoviePoster] = []
    
    override func loadView() {
        let layout = UICollectionViewFlowLayout()
        view = UICollectionView(frame: .zero, collectionViewLayout: layout)
        
        super.loadView()        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.startPresenting(view: self)
    }
    
    func render(gallery: Gallery) {
        movies = gallery.moviePosters
    }
    
    func renderError(message: String?) {
//        guard let errorMessage = message else {
//            label.text = "Something went wrong"
//            return
//        }
//        label.text = errorMessage
    }
}

extension GalleryViewController {
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
            return movies.count
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "movie_poster", for: indexPath)
        cell.backgroundColor = .green
        return cell
    }
}
