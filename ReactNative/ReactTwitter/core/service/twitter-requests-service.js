// @flow

var OauthHelper = require('../service/oauth-helper.js')
var NetworkCallsManager = require('./network-calls-manager.js')
var AuthenticationService = require('./authentication-service.js')
const config = require('./config.json')

class TwitterRequestsService {
  authService: AuthenticationService;
  callsManager: NetworkCallsManager;

  constructor (authService: AuthenticationService) {
    this.authService = authService
    this.callsManager = new NetworkCallsManager()
  }

  requestToken () {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let url = config.BASE_URL + config.REQUEST_TOKEN

    let parameters: any = this._getBaseParams()
    parameters['oauth_callback'] = config.CALLBACK_URL

    return this.callsManager.makeCall(url,
      {
        method: 'POST',
        headers: {
          'Authorization': ouathHelper.buildAuthorizationHeader('post', url, parameters, [], '')
        }
      })
      .then((response) => { return response.text() })
      .then((text) => { return this._getTokenDataFromResponse(text) })
  }

  getAccessToken (oauthToken: string, verifierToken: string) {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let url = config.BASE_URL + config.ACCESS_TOKEN

    let parameters: any = this._getBaseParams()
    parameters['oauth_token'] = oauthToken
    parameters['oauth_verifier'] = verifierToken

    return this.callsManager.makeCall(url,
      {
        method: 'POST',
        headers: {
          'Authorization': ouathHelper.buildAuthorizationHeader('post', url, parameters, '', '')
        }
      })
      .then((response) => { return response.text() })
      .then((text) => {
        let tokenData = this._getTokenDataFromResponse(text)
        return this.authService.updateWithTokenData(tokenData)
      })
  }

  getHomeTimeline () {
    let url = config.BASE_URL + config.HOME_TIMELINE

    let parameters: any = this._getBaseParams()
    parameters['oauth_token'] = this.authService.getOAuthToken()

    let queryParams = { 'screen_name': this.authService.getUsername() }
    return this._getResource(url, parameters, queryParams)
  }

  getTweet (tweetId: string) {
    let url = config.BASE_URL + config.TWEET_DETAIL

    let parameters: any = this._getBaseParams()
    parameters['oauth_token'] = this.authService.getOAuthToken()

    let queryParams = { 'id': tweetId }

    return this._getResource(url, parameters, queryParams)
  }

  _getResource (baseURL: string, parameters: any, queryParams: any) {
    let ouathHelper = new OauthHelper(config.CONSUMER_SECRET)
    let authHeader = ouathHelper
     .buildAuthorizationHeader('get', baseURL, parameters, queryParams, this.authService.getSecretToken())

    let finalUrl = baseURL
    for (let key in queryParams) {
      finalUrl += (finalUrl === baseURL) ? '?' : '&'
      finalUrl += `${key}=${queryParams[key]}`
    }

    return this.callsManager.makeCall(finalUrl,
      {
        method: 'GET',
        headers: {
          'Authorization': authHeader
        }
      })
    .then((response) => { return response.json() })
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

  _getTokenDataFromResponse (response: string) {
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
