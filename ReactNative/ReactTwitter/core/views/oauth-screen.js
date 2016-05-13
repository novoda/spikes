import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  Linking
} from 'react-native'

var Button = require('react-native-button')
var TwitterRequestsService = require('../service/twitter-requests-service.js')
var DeepLinkingFacade = require('../service/deep-linking-facade')
var OauthHelper = require('../service/oauth-helper.js')

var OauthView = React.createClass({
  getInitialState () {
    return {
      accessToken: '',
      oauthVerifier: '',
      facade: new DeepLinkingFacade()
    }
  },

  render () {
    return (
      <View style={styles.container}>
      <Button
        style={styles.button}
        styleDisabled={styles.button_disabled}
        onPress={this._buttonClicked}> Request Access Token </Button>
        <Text style={styles.normal} numberOfLines={4}>
          Access Token: {"\n"}{this.state.accessToken}{"\n"}
          Oauth Verifier: {"\n"}{this.state.oauthVerifier}
        </Text>
      </View>
    )
  },

  _buttonClicked () {
    let helper = new TwitterRequestsService()
    helper.requestToken()
        .then((tokenData) => {
          let oauthToken = tokenData.oauth_token
          this.setState({accessToken: oauthToken})
          this._browserAuthenticationWithToken(oauthToken)
        })
        .catch(console.warn)
  },

  componentWillUnmount () {
    this.state.facade.stopListeningForDeepLinking()
  },

  _browserAuthenticationWithToken (oauthToken) {
    this.state.facade.listenForDeepLinking().then((uri) => {
      let parsedURI = OauthHelper.getOauthTokenAndVerifierFromURLCallback(uri)
      this.setState({
        accessToken: parsedURI.oauth_token,
        oauthVerifier: parsedURI.oauth_verifier
      })
      this.state.facade.stopListeningForDeepLinking()
    })
    Linking.openURL('https://api.twitter.com/oauth/authenticate?oauth_token=' + oauthToken)
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

module.exports = OauthView
