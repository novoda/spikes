#import "RCTNovodaButtonManager.h"
#import "RCTNovodaButton.h"
#import "UIColor+additions.h"

@implementation RCTNovodaButtonManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  return [RCTNovodaButton buttonWithType:UIButtonTypeSystem];
}

RCT_EXPORT_VIEW_PROPERTY(enabled, BOOL)

RCT_CUSTOM_VIEW_PROPERTY(text, NSString, UIButton)
{
  [view setTitle:json forState: UIControlStateNormal];
}

RCT_CUSTOM_VIEW_PROPERTY(textColor, NSString, UIButton)
{
  [view setTitleColor:[UIColor colorFromHexString:json] forState:UIControlStateNormal];
}

RCT_CUSTOM_VIEW_PROPERTY(backgroundImage, NSString, UIButton)
{
  UIImage *normalImage = [UIImage imageNamed:[NSString stringWithFormat:@"%@Normal", json]];
  if (normalImage) {
    [view setBackgroundImage:normalImage forState:UIControlStateNormal];
  }

  UIImage *pressedImage = [UIImage imageNamed:[NSString stringWithFormat:@"%@Pressed", json]];
  if (pressedImage) {
    [view setBackgroundImage:pressedImage forState:UIControlStateHighlighted];
  }

  UIImage *disabledImage = [UIImage imageNamed:[NSString stringWithFormat:@"%@Disabled", json]];
  if (disabledImage) {
    [view setBackgroundImage:disabledImage forState:UIControlStateDisabled];
  }

}

@end
