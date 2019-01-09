//
//  ViewController.swift
//  Movies
//
//  Created by Tobias Heine on 09.01.19.
//  Copyright © 2019 Novoda. All rights reserved.
//

import UIKit
import common

class ViewController: UIViewController {
    
    private let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))

    override func viewDidLoad() {
        super.viewDidLoad()
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        view.addSubview(label)
        
        label.text = Platform().name
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

