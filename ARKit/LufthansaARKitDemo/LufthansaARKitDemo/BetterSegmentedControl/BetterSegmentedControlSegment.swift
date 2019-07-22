//
//  BetterSegmentedControlSegment.swift
//  BetterSegmentedControl
//
//  Created by George Marmaridis on 08/10/2017.
//

import Foundation
import UIKit

public protocol BetterSegmentedControlSegment {
    var normalView: UIView { get }
    var selectedView: UIView { get }
}
