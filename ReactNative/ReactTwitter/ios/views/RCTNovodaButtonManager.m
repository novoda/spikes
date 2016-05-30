//
//  iOSButton.m
//  ReactTwitter
//
//  Created by Giuseppe Basile on 26/05/2016.
//  Copyright © 2016 Facebook. All rights reserved.
//

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

RCT_CUSTOM_VIEW_PROPERTY(backgroundNormal, NSString, UIButton)
{
  if (json && [(NSString *)json length]) {
    [view setBackgroundImage:[UIImage imageNamed:json] forState:UIControlStateNormal];
  }
}

RCT_CUSTOM_VIEW_PROPERTY(backgroundPressed, NSString, UIButton)
{
  if (json && [(NSString *)json length]) {
    [view setBackgroundImage:[UIImage imageNamed:json] forState:UIControlStateHighlighted];
  }
}

RCT_CUSTOM_VIEW_PROPERTY(backgroundDisabled, NSString, UIButton)
{
  if (json && [(NSString *)json length]) {
    [view setBackgroundImage:[UIImage imageNamed:json] forState:UIControlStateDisabled];
  }
}

@end
