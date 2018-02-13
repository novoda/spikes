import UIKit
import KotlinGameOfLife

class UICell: UIButton {
    
    let position:KGOLPositionEntity?
    
    init(frame: CGRect,position:KGOLPositionEntity) {
        self.position = position
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.position = nil
        super.init(coder: aDecoder)
    }
    
}
