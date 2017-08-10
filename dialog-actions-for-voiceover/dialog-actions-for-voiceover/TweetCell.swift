//
//  TweetCell.swift
//  dialog-actions-for-voiceover
//
//  Created by Ataul Munim on 10/08/2017.
//  Copyright © 2017 Novoda. All rights reserved.
//

import UIKit

class TweetCell: UITableViewCell {
    
    var avatarImageView = UIImageView()
    var usernameLabel = UILabel()
    var bodyLabel = UILabel()
    var timeLabel = UILabel()
    var replyButton = UIButton()
    var retweetButton = UIButton()
    var likeButton = UIButton()
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:)")
    }
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: .default, reuseIdentifier: reuseIdentifier)
        self.backgroundColor = .blue
        setupHierarchy()
        setupViews()
        layoutViews()
    }
    
    private func setupHierarchy() {
        addSubview(avatarImageView)
        addSubview(timeLabel)
        addSubview(usernameLabel)
//        addSubview(bodyLabel)
        
//        addSubview(replyButton)
//        addSubview(retweetButton)
//        addSubview(likeButton)
        
        subviews.forEach { view in
            view.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    private func setupViews() {
        usernameLabel.text = "username"
        bodyLabel.text = "tweet body"
        timeLabel.text = "12:01"
        replyButton.setTitle("reply", for: .normal)
        retweetButton.setTitle("rt", for: .normal)
        likeButton.setTitle("like", for: .normal)
        avatarImageView.backgroundColor = UIColor.black
        
        usernameLabel.sizeToFit()
        usernameLabel.numberOfLines = 1
        
        timeLabel.sizeToFit()
        timeLabel.numberOfLines = 1
        
        bodyLabel.sizeToFit()
        bodyLabel.numberOfLines = 0
        bodyLabel.lineBreakMode = .byWordWrapping
    }
    
    private func layoutViews() {
        addConstraints([
            constrainToTweetCell(attribute: .left, view: avatarImageView),
            constrainToTweetCell(attribute: .top, view: avatarImageView),
            constrainToConstant(attribute: .width, value: 40, view: avatarImageView),
            constrainToConstant(attribute: .height, value: 40, view: avatarImageView)
        ])
        
        addConstraints([
            constrainToTweetCell(attribute: .right, view: timeLabel),
            constrainToTweetCell(attribute: .top, view: timeLabel),
        ])
        
        addConstraints([
            NSLayoutConstraint(
                item: usernameLabel,
                attribute: .right,
                relatedBy: .lessThanOrEqual,
                toItem: timeLabel,
                attribute: .left,
                multiplier: 1,
                constant: 0
            ),
            constrainToTweetCell(attribute: .top, view: usernameLabel),
            constrain(view: usernameLabel, attribute: .left, otherView: avatarImageView, otherAttribute: .right),
        ])
    }
    
    private func constrainToConstant(attribute: NSLayoutAttribute, value: CGFloat, view: UIView) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: nil,
            attribute: .notAnAttribute,
            multiplier: 1,
            constant: value
        )
    }
    
    private func constrain(view: UIView, attribute: NSLayoutAttribute, otherView: UIView, otherAttribute: NSLayoutAttribute) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: otherView,
            attribute: otherAttribute,
            multiplier: 1,
            constant: 0
        )
    }
    
    private func constrainToTweetCell(attribute: NSLayoutAttribute, view: UIView) -> NSLayoutConstraint {
        return NSLayoutConstraint(
            item: view,
            attribute: attribute,
            relatedBy: .equal,
            toItem: self,
            attribute: attribute,
            multiplier: 1,
            constant: 0
        )
    }
}
