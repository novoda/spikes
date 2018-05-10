//
//  ViewController.swift
//  HelloTorchIOS
//
//  Created by Luis Valle on 02/05/2018.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    let switchCounter = SwitchCounter()
    
    @IBAction func toggleTorch(sender: UIButton) {
        switchCounter.run()
    }
}
