//
//  ViewController.swift
//  Movies
//
//  Created by Tobias Heine on 21.12.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let label = UILabel(frame: .zero)
        view.addSubview(label)
        
        label.pinToSuperviewEdges()
        label.textAlignment = .center
        label.text = "Foo"
        
        
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

