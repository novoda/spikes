import React, { Component } from 'react'
import {
  StyleSheet,
  View,
  Text
} from 'react-native'

var Button = require('react-native-button')
var DeepLinkingFacade = require('../service/deep-linking-facade')

var HelloWorldView = React.createClass({

  getInitialState() {
    return {
      facade: new DeepLinkingFacade()
    }
  },

  render () {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Hello World!
        </Text>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._loginButtonClicked}> Login </Button>
      </View>
    )
  },

  _loginButtonClicked () {
    this.state.facade.listenForDeepLinking().then((uri) => console.log(uri))    
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  welcome: {
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

module.exports = HelloWorldView
