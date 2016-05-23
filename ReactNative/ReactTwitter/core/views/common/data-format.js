var dateFormat = require('dateformat')
var tinyHumanTime = require('tiny-human-time')
class NovodaDataFormat {
  static longTime (time) {
    return dateFormat(time, 'mmmm dS, yyyy, h:MM:ss TT')
  }

  static elapsedTime (time) {
    return tinyHumanTime(new Date(), time) + ' ago'
  }
}

module.exports = NovodaDataFormat
