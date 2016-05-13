import HmacSHA1 from 'crypto-js/hmac-sha1'
import CryptoJS from 'crypto-js'
import PercentEncoder from './percent-encoder.js'

class OauthHelper {
  constructor (consumerSecret) {
    this.consumerSecret = consumerSecret
  }

  buildAuthorizationHeader (method, url, params, oauthTokenSecret) {
    let output = 'OAuth '
    let signature = this.getSigningKey(method, url, params, oauthTokenSecret)
    for (let key in params) {
      output += PercentEncoder.encode(key) + '="' + PercentEncoder.encode(params[key]) + '", '
    }
    output += 'oauth_signature="' + PercentEncoder.encode(signature) + '"'
    return output
  }

  getSigningKey (method, url, params, oauthTokenSecret) {
    let signingKey = PercentEncoder.encode(this.consumerSecret) + '&' + PercentEncoder.encode(oauthTokenSecret)
    let signatureBase = OauthHelper._getSignatureBase(method, url, params)
    return CryptoJS.enc.Base64.stringify(HmacSHA1(signatureBase, signingKey))
  }

  static _getSignatureBase (method, url, params) {
    return method.toUpperCase() + '&' + PercentEncoder.encode(url) + '&' + PercentEncoder.encode(OauthHelper._collectParameters(params))
  }

  static _collectParameters (params) {
    let encodedParams = OauthHelper._percentEncodeParams(params)
    OauthHelper._sortEncodedParams(encodedParams)
    return OauthHelper._joinParams(encodedParams)
  }

  static _percentEncodeParams (params) {
    let encodedParams = []
    for (let key in params) {
      encodedParams.push({
        key: `${PercentEncoder.encode(key)}`,
        value: `${PercentEncoder.encode(params[key])}`
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
