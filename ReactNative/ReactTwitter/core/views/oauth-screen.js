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
      tokenData: {},
      facade: new DeepLinkingFacade(),
      twitterService: new TwitterRequestsService()
    }
  },

  render () {
    return (
      <View style={styles.container}>
      <Button
        style={styles.button}
        styleDisabled={styles.button_disabled}
        onPress={this._buttonClicked}> Request Access Token </Button>
        <Text style={styles.normal}>
          STEP 1 Access Token: {"\n"}{this.state.accessToken}{"\n"}{"\n"}
          STEP 2 Oauth Verifier: {"\n"}{this.state.oauthVerifier}{"\n"}{"\n"}
          STEP 3 Token Data: {"\n"}{JSON.stringify(this.state.tokenData)}
        </Text>
      </View>
    )
  },

  _buttonClicked () {
    this.setState({
      accessToken: '',
      oauthVerifier: '',
      tokenData: {}
    })
    this.state.twitterService.requestToken()
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

      this.state.twitterService.getAccessToken(parsedURI.oauth_token, parsedURI.oauth_verifier)
        .then((tokenData) => {
          this.setState({tokenData: tokenData})
        })
    })
    Linking.openURL('https://api.twitter.com/oauth/authenticate?oauth_token=' + oauthToken)
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    marginTop: 20
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
