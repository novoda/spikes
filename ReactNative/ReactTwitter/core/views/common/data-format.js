var dateFormat = require('dateformat')

class NovodaDataFormat {
  static longTime(time) {
    return dateFormat(time, 'mmmm dS, yyyy, h:MM:ss TT')
  }
}

module.exports = NovodaDataFormat
