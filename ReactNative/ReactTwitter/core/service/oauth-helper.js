import HmacSHA1 from 'crypto-js/hmac-sha1'
import CryptoJS from 'crypto-js'
import PercentEncoder from './percent-encoder.js'

class OauthHelper {
  constructor (consumerSecret) {
    this.consumerSecret = consumerSecret
  }

  getSigningKey (method, url, params, oauthTokenSecret) {
    var signingKey = this.consumerSecret + '&' + oauthTokenSecret
    console.log(`signing key: ${signingKey}`)
    let signatureBase = this._getSignatureBase(method, url, params)
    console.log(`signatureBase: ${signatureBase}`)
    console.log(HmacSHA1(signatureBase, signingKey))
    return CryptoJS.enc.Base64.stringify(HmacSHA1(signatureBase, signingKey))
  }

  _getSignatureBase (method, url, params) {
    return method.toUpperCase() + '&' + PercentEncoder.encode(url) + '&' + PercentEncoder.encode(this._collectParameters(params))
  }

  _collectParameters (params) {
    let encodedParams = this._percentEncodeParams(params)
    this._sortEncodedParams(encodedParams)
    return this._joinParams(encodedParams)
  }

  _percentEncodeParams (params) {
    let encodedParams = []
    for (let key in params) {
      encodedParams.push({
        key: `${PercentEncoder.encode(key)}`,
        value: `${PercentEncoder.encode(params[key])}`
      })
    }
    return encodedParams
  }

  _sortEncodedParams (params) {
    params.sort((first, second) => {
      return (first.key > second.key) ? 1 : ((second.key > first.key) ? -1 : 0)
    })
  }

  _joinParams (params) {
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
