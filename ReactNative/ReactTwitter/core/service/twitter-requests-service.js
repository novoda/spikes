var OauthHelper = require('../service/oauth-helper.js')

const config = require('./config.json')

class TwitterRequestsService {

  requestToken () {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let time = new Date().getTime() / 1000 | 0
    let nonce = OauthHelper.generateNonce()
    let url = config.BASE_URL + config.REQUEST_TOKEN
    const params = {
      'oauth_callback': config.CALLBACK_URL,
      'oauth_consumer_key': config.CONSUMER_KEY,
      'oauth_nonce': nonce,
      'oauth_signature_method': 'HMAC-SHA1',
      'oauth_timestamp': time,
      'oauth_version': '1.0'
    }

    return fetch(url, // eslint-disable-line no-undef
      {
        method: 'POST',
        headers: {
          'Authorization': ouathHelper.buildAuthorizationHeader('post', url, params, '')
        }
      })
      .then((response) => { return response.text() })
      .then((text) => { return this._getTokenDateFromResponse(text) })
  }

  _getTokenDateFromResponse (response) {
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
