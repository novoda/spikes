//
//  ViewController.swift
//  DrawerViewExample
//
//  Created by Alex Curran on 04.09.18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import UIKit

class ViewController: UIViewController, DrawerViewDelegate {
    
    private let peekView = UILabel()
    private let contentView = UILabel()
    private let dimmingView = UIView()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        setUpPeekView()
        setUpContentView()
        setUpDimmingView()
        
        let drawerView = DrawerView(peekView: peekView, contentView: contentView)
        drawerView.delegate = self
        view.addSubview(drawerView)
        
        // Constrain the drawer to everything except the top, so it is draggable
        drawerView.edgesToSuperview(excluding: [.top])
        
        // The user of the drawer view is required to set up the peek view's constraints
        peekView.leadingToSuperview()
        peekView.trailingToSuperview()
        let animatingConstraint = peekView.bottom(to: view)
        
        peekView.height(56)
        
        // Set the constraint that will be animated as part of the interaction
        drawerView.animatingConstraint = animatingConstraint
    }
    
    func filterViewDidCollapse(_ filterView: DrawerView) {
        UIViewPropertyAnimator(duration: 0.1, curve: .easeIn) {
            self.dimmingView.alpha = 0
        }.startAnimation()
    }
    
    func filterViewWillExpand(_ filterView: DrawerView) {
        UIViewPropertyAnimator(duration: 0.1, curve: .easeOut) {
            self.dimmingView.alpha = 1
        }.startAnimation()
    }
    
    private func setUpPeekView() {
        peekView.text = "Tap or drag to expand"
        peekView.textAlignment = .center
        peekView.backgroundColor = .red
    }
    
    private func setUpContentView() {
        contentView.backgroundColor = .green
        contentView.text = loremIpsum()
        contentView.numberOfLines = 0
    }

    private func setUpDimmingView() {
        view.addSubview(dimmingView)
        dimmingView.backgroundColor = UIColor.black.withAlphaComponent(0.2)
        dimmingView.alpha = 0
        dimmingView.edgesToSuperview()
    }
    
    private func loremIpsum() -> String {
        return """
        This adds a simple "DrawerView" or bottom sheet behaviour to iOS projects.
        
        The integration is relatively simple, and relies on constraints making it a bit more robust.
        
        To integrate it you must:
        - Add a "peek view" which is the view always shown (red in the movie)
        - Add a "content view" which can be shown when dragged (green; this is automatically scrollable)
        - Set the constraint which will be animatable.
        
        The API includes a basic delegate which can be used to add a dimming background view, as shown.
        """
    }

}

