import HmacSHA1 from 'crypto-js/hmac-sha1'
import CryptoJS from 'crypto-js'
import PercentEncoder from './percent-encoder.js'

class OauthHelper {
  constructor (consumerSecret) {
    this.consumerSecret = consumerSecret
  }

  buildAuthorizationHeader (method, url, params, oauthTokenSecret) {
    return buildAuthorizationHeader (method, url, params, [], oauthTokenSecret)
  }

  buildAuthorizationHeader (method, url, params, queryParams, oauthTokenSecret) {
    let output = 'OAuth '
    let signature = this.getSigningKey(method, url, params, queryParams, oauthTokenSecret)
    for (let key in params) {
      output += PercentEncoder.encode(key) + '="' + PercentEncoder.encode(params[key]) + '", '
    }
    output += 'oauth_signature="' + PercentEncoder.encode(signature) + '"'
    return output
  }

  getSigningKey (method, url, params, queryParams, oauthTokenSecret) {
    let signingKey = PercentEncoder.encode(this.consumerSecret) + '&' + PercentEncoder.encode(oauthTokenSecret)
    let signatureBase = OauthHelper._getSignatureBase(method, url, params, queryParams)
    return CryptoJS.enc.Base64.stringify(HmacSHA1(signatureBase, signingKey))
  }

  // Returns an object with oauth_token and oauth_verifier
  static getOauthTokenAndVerifierFromURLCallback (urlCallback) {
    let prefix = 'react-twitter-oauth://callback?'
    let content = urlCallback.substr(prefix.length)

    let result = {}
    content.split('&').forEach((pair) => {
      let values = pair.split('=')
      result[values[0]] = values[1]
    })

    return result
  }

  static _getSignatureBase (method, url, params, queryParams) {
    return method.toUpperCase() + '&' + PercentEncoder.encode(url) + '&'
      + PercentEncoder.encode(OauthHelper._collectParameters(params, queryParams))
  }

  static _collectParameters (params, queryParams) {
    let encodedParams = OauthHelper._percentEncodeParams(params, queryParams)
    OauthHelper._sortEncodedParams(encodedParams)
    return OauthHelper._joinParams(encodedParams)
  }

  static _percentEncodeParams (params, queryParams) {
    let encodedParams = []
    for (let key in params) {
      encodedParams.push({
        key: `${PercentEncoder.encode(key)}`,
        value: `${PercentEncoder.encode(params[key])}`
      })
    }
    for (let key in queryParams) {
      encodedParams.push({
        key: `${PercentEncoder.encode(key)}`,
        value: `${PercentEncoder.encode(queryParams[key])}`
      })
    }
    return encodedParams
  }

  static _sortEncodedParams (params) {
    params.sort((first, second) => {
      return (first.key > second.key) ? 1 : ((second.key > first.key) ? -1 : 0)
    })
  }

  static _joinParams (params) {
    let output = ''
    params.forEach((param) => {
      if (output.length > 0) {
        output += '&'
      }
      output += param.key + '=' + param.value
    })
    return output
  }

  static generateNonce () {
    var text = ''
    var length = 32
    var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    for (var i = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length))
    }
    return text
  }
}

module.exports = OauthHelper
