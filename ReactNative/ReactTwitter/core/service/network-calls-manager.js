class NetworkCallsManager {
  makeCall (url, options) {
    return fetch(url, options) // eslint-disable-line no-undef
  }
}

module.exports = NetworkCallsManager
