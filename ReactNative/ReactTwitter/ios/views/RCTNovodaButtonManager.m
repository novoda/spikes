//
//  iOSButton.m
//  ReactTwitter
//
//  Created by Giuseppe Basile on 26/05/2016.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "RCTNovodaButtonManager.h"
#import "RCTNovodaButton.h"

@implementation RCTNovodaButtonManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  return [RCTNovodaButton buttonWithType:UIButtonTypeSystem];
}

RCT_CUSTOM_VIEW_PROPERTY(text, NSString, UIButton)
{
  [view setTitle:json forState: UIControlStateNormal];
}

@end
