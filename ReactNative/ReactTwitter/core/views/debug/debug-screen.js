// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Navigator
} from 'react-native'

var Button = require('react-native-button')

var DebugScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  render () {
    return (
      <View style={styles.container}>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._pushDeepLinking}>Deep Linking</Button>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._pushOauth}>Oauth</Button>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._pushPushNotifications}>Push Notifications</Button>
      </View>
    )
  },

  _pushDeepLinking () {
    this.props.navigator.push({id: 'deep-linking-identifier'})
  },

  _pushOauth () {
    this.props.navigator.push({id: 'oauth-screen-identifier'})
  },

  _pushPushNotifications () {
    this.props.navigator.push({id: 'push-notifications-screen-identifier'})
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    backgroundColor: '#F5FCFF'
  },
  button: {
    fontSize: 20,
    color: 'black'
  },
  button_disabled: {
    fontSize: 20,
    color: 'grey'
  }
})

module.exports = DebugScreenView
