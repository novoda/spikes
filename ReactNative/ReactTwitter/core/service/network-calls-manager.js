// @flow

class NetworkCallsManager {
  makeCall (url: string, options: any) {
    return fetch(url, options) // eslint-disable-line no-undef
  }
}

module.exports = NetworkCallsManager
