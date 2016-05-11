import { NativeModules } from 'react-native'

var oAuthIntentAndroid = NativeModules.OauthIntentAndroid

class DeepLinkingFacade {

  static newInstance () {
    return new DeepLinkingFacade()
  }

  listenForDeepLinking () {
    return oAuthIntentAndroid.registerForDeepLinking()
      .then((event) => event.url)
  }

  stopListeningForDeepLinking () {
    // no-op
  }
}

module.exports = DeepLinkingFacade
