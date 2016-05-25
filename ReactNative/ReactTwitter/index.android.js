/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import { AppRegistry } from 'react-native'

var MainNavigator = require('./core/views/main-navigator.js')
var PushNotificationService = require('./core/service/push-notifications-service.js')

PushNotificationService.init()

AppRegistry.registerComponent('ReactTwitter', () => MainNavigator)
