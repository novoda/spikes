//
//  ViewController.swift
//  DrawerViewExample
//
//  Created by Alex Curran on 04.09.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let peekView = UILabel()
        peekView.text = "Tap or drag to expand"
        peekView.textAlignment = .center
        peekView.backgroundColor = .red
        
        let contentView = UIView()
        contentView.backgroundColor = .green
        
        let drawerView = DrawerView(peekView: peekView, contentView: contentView)
        view.addSubview(drawerView)
        drawerView.edgesToSuperview(excluding: [.top])
        
        peekView.edgesToSuperview(excluding: [.top, .bottom])
        let animatingConstraint = peekView.bottom(to: view)
        
        contentView.height(600)
        peekView.height(56)
        
        drawerView.animatingConstraint = animatingConstraint
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

