import React from 'react'
import { NativeModules } from 'react-native'

var oAuthIntentAndroid = NativeModules.OauthIntentAndroid

class DeepLinkingFacade {
  listenForDeepLinking() {
    return oAuthIntentAndroid.registerForDeepLinking()
  }
}

module.exports = DeepLinkingFacade
