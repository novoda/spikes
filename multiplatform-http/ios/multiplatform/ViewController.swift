//
//  ViewController.swift
//  multiplatform
//
//  Created by Tobias Heine on 07.11.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit
import common

class ViewController: UIViewController, PresenterView {

    private let presenter = Presenter(api: Api())
    private let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
        
    override func viewDidLoad() {
        super.viewDidLoad()
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        view.addSubview(label)
        presenter.startPresenting(view: self)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        presenter.stopPresenting()
        viewWillDisappear(animated)
    }
    
    func render(recipe: Recipe) {
        label.text = recipe.header.title
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
