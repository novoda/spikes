var OauthHelper = require('../service/oauth-helper.js')
var NetworkCallsManager = require('./network-calls-manager.js')

const config = require('./config.json')

class TwitterRequestsService {

  constructor () {
    this.callsManager = new NetworkCallsManager()
  }

  requestToken () {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let url = config.BASE_URL + config.REQUEST_TOKEN

    let parameters = this._getBaseParams()
    parameters['oauth_callback'] = config.CALLBACK_URL

    return this.callsManager.makeCall(url,
      {
        method: 'POST',
        headers: {
          'Authorization': ouathHelper.buildAuthorizationHeader('post', url, parameters, '')
        }
      })
      .then((response) => { return response.text() })
      .then((text) => { return this._getTokenDataFromResponse(text) })
  }

  getAccessToken (oauthToken, verifierToken) {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let url = config.BASE_URL + config.ACCESS_TOKEN

    let parameters = this._getBaseParams()
    parameters['oauth_token'] = oauthToken
    parameters['oauth_verifier'] = verifierToken

    return this.callsManager.makeCall(url,
      {
        method: 'POST',
        headers: {
          'Authorization': ouathHelper.buildAuthorizationHeader('post', url, parameters, '')
        }
      })
      .then((response) => { return response.text() })
      .then((text) => { return this._getTokenDataFromResponse(text) })
  }

  _getBaseParams () {
    let time = new Date().getTime() / 1000 | 0
    let nonce = OauthHelper.generateNonce()
    return {
      'oauth_consumer_key': config.CONSUMER_KEY,
      'oauth_nonce': nonce,
      'oauth_signature_method': 'HMAC-SHA1',
      'oauth_timestamp': time,
      'oauth_version': '1.0'
    }
  }

  _getTokenDataFromResponse (response) {
    let result = {}
    let data = response.split('&')
    data.forEach((pair) => {
      let pairValues = pair.split('=')
      result[pairValues[0]] = pairValues[1]
    })
    return result
  }
}

module.exports = TwitterRequestsService
