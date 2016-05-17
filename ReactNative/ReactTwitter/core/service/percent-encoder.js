class PercentEncoder {
  static encode (toEncode) {
    return encodeURIComponent(toEncode).replace(/!/g, '%21')
  }
}

module.exports = PercentEncoder
