// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Text
} from 'react-native'

var Button = require('react-native-button')
// $FlowFixMe: flow doesn't understand dynamic import
var DeepLinkingFacade = require('../../service/deep-linking-facade')
import AndroidBackNavigationMixin from '../mixins/android-back-navigation'

var DeepLinkingView = React.createClass({
  mixins: [AndroidBackNavigationMixin],
  statics: {
    navigatorID () {
      return 'deep-linking-identifier'
    },
    navigatorTitle () {
      return 'Deep Linking'
    }
  },

  getInitialState () {
    return {
      facade: new DeepLinkingFacade(),
      deepLinkUrl: ''
    }
  },

  render () {
    return (
      <View style={styles.container}>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._buttonClicked}> Start Listening for deep linking </Button>
        <Text style={styles.normal} numberOfLines={2}>
          The deep link url is {"\n"}{this.state.deepLinkUrl}
        </Text>
      </View>
    )
  },

  componentWillUnmount () {
    this.state.facade.stopListeningForDeepLinking()
  },

  _buttonClicked () {
    this.state.facade.listenForDeepLinking().then((uri) => { this.setState({ deepLinkUrl: uri }) })
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  normal: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
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

module.exports = DeepLinkingView
