//
//  UIColor+additions.m
//  ReactTwitter
//
//  Created by Giuseppe Basile on 30/05/2016.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "UIColor+additions.h"

@implementation UIColor (additions)

+ (UIColor *)colorFromHexString:(NSString *)hexString
{
  unsigned rgbValue = 0;
  NSScanner *scanner = [NSScanner scannerWithString:hexString];
  [scanner setScanLocation:1]; // bypass '#' character
  [scanner scanHexInt:&rgbValue];
  return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

@end
