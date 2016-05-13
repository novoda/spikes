import React from 'react'
import {
  StyleSheet,
  View,
  Text
} from 'react-native'

var Button = require('react-native-button')
var TwitterRequestsService = require('../service/twitter-requests-service.js')

var DeepLinkingView = React.createClass({
  getInitialState () {
    return {
      accessToken: ''
    }
  },

  render () {
    return (
      <View style={styles.container}>
      <Button
        style={styles.button}
        styleDisabled={styles.button_disabled}
        onPress={this._buttonClicked}> Request Access Token </Button>
        <Text style={styles.normal} numberOfLines={2}>
          Access Token: {"\n"}{this.state.accessToken}
        </Text>
      </View>
    )
  },

  _buttonClicked () {
    let helper = new TwitterRequestsService()
    helper.requestToken()
        .then((tokenData) => {
          this.setState({accessToken: tokenData.oauth_token})
        })
        .catch(console.warn)
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
    fontSize: 14,
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
