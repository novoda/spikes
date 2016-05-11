import React from 'react'
import { Linking } from 'react-native'

class DeepLinkingFacade {

  listenForDeepLinking() {
    Linking.removeEventListener('url', this._handleOpenURL);
    Linking.addEventListener('url', this._handleOpenURL);
    return new Promise(function (resolve, reject) {
      this.resolvePromise = resolve;
    });
  }

  _handleOpenURL(event) {
    Linking.removeEventListener('url', this._handleOpenURL);
    this.resolvePromise(event.url)
  }
}

module.exports = DeepLinkingFacade
