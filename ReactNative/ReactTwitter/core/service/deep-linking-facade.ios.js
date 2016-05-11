import { Linking } from 'react-native'

class DeepLinkingFacade {

  static newInstance () {
    return new DeepLinkingFacade(Linking)
  }

  constructor (linking) {
    this.linking = linking
  }

  listenForDeepLinking () {
    this.linking.removeEventListener('url', this._handleOpenURL)
    this.linking.addEventListener('url', this._handleOpenURL)
    return new Promise(function (resolve, reject) {
      this.resolvePromise = resolve
    })
  }

  stopListeningForDeepLinking () {
    this.linking.removeEventListener('url', this._handleOpenURL)
  }

  _handleOpenURL (event) {
    this.resolvePromise(event.url)
  }
}

module.exports = DeepLinkingFacade
