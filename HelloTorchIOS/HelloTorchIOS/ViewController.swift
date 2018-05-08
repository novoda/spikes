//
//  ViewController.swift
//  HelloTorchIOS
//
//  Created by Luis Valle on 02/05/2018.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    let strobeLights = StrobeLights()
    
    @IBAction func toggleTorch(sender: UIButton) {
        strobeLights.toggleStrobe()
    }
}
