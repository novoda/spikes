import { NativeModules } from 'react-native'

var oAuthIntentAndroid = NativeModules.OauthIntentAndroid

class DeepLinkingFacade {

  static newInstance () {
    return new DeepLinkingFacade()
  }

  listenForDeepLinking () {
    return oAuthIntentAndroid.registerForDeepLinking()
      .then(function (event) { return event.url })
  }
}

module.exports = DeepLinkingFacade
