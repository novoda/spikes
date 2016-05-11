import React from 'react'
import {
  NativeModules
} from 'react-native'

var oAuthIntentAndroid = NativeModules.OauthIntentAndroid

class DeepLinkingFacade {
  listenForDeepLinking() {
    console.log('I\'m android')
    return oAuthIntentAndroid.registerForDeepLinking()
  }
}

module.exports = DeepLinkingFacade
