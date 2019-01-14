//
//  ViewController.swift
//  Movies
//
//  Created by Tobias Heine on 09.01.19.
//  Copyright © 2019 Novoda. All rights reserved.
//

import UIKit
import common

class GalleryViewController: UIViewController, GalleryPresenterView {
    
    private let presenter = GalleryDependencyProvider(networkingDependencyProvider: NetworkingDependencyProvider()).providerPresenter()
    private let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))

    override func viewDidLoad() {
        super.viewDidLoad()
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        view.addSubview(label)
        presenter.startPresenting(view: self)
    }
    
    func render(gallery: Gallery) {
        label.text = "Gallery with \(gallery.moviePosters.count) posters"
    }
    
    func renderError(exception: KotlinException) {
        label.text = exception.message
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

