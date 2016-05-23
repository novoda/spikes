import { AsyncStorage } from 'react-native'
var PushNotification = require('react-native-push-notification');


const STORAGE_KEY = "@PushNotificationToken:key"

class PushNotificationsService {
  static init () {
    PushNotification.configure({

        // (optional) Called when Token is generated (iOS and Android)
        onRegister: function(token) {
            console.log( 'TOKEN:', token );
            AsyncStorage.setItem(STORAGE_KEY, token.token)
        },

        // (required) Called when a remote or local notification is opened or received
        onNotification: function(notification) {
            console.log( 'NOTIFICATION:', notification );
        },

        // ANDROID ONLY: (optional) GCM Sender ID.
        senderID: "865490210861",

        // IOS ONLY (optional): default: all - Permissions to register.
        permissions: {
            alert: true,
            badge: true,
            sound: true
        },

        // Should the initial notification be popped automatically
        // default: true
        popInitialNotification: true,

        /**
          * IOS ONLY: (optional) default: true
          * - Specified if permissions will requested or not,
          * - if not, you must call PushNotificationsHandler.requestPermissions() later
          */
        requestPermissions: true,
    })
  }
}

module.exports = PushNotificationsService
