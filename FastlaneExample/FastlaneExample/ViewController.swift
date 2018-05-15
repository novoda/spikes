//
//  ViewController.swift
//  FastlaneExample
//
//  Created by Berta Devant on 13/03/2018.
//  Copyright © 2018 Berta Devant. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var button: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    @IBAction func buttonPressed(_ sender: Any) {
        button.titleLabel?.text = "Pressed"
    }

}

