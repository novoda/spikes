import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  Navigator,
  Linking
} from 'react-native'

var Button = require('react-native-button')
var TwitterRequestsService = require('../service/twitter-requests-service.js')
var DeepLinkingFacade = require('../service/deep-linking-facade')
var OauthHelper = require('../service/oauth-helper.js')
var AuthenticationService = require('../service/authentication-service.js')

var LoginScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    return {
      facade: new DeepLinkingFacade(),
      twitterService: new TwitterRequestsService(),
      authenticationService: new AuthenticationService()
    }
  },

  componentWillUnmount () {
    this.state.facade.stopListeningForDeepLinking()
  },

  render () {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Welcome to our awesome React-Native Twitter client.
          Press the button below to start the login flow.</Text>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._startLogin}>Login!!!</Button>
      </View>
    )
  },

  _startLogin () {
    this.state.twitterService.requestToken()
        .then((tokenData) => {
          let oauthToken = tokenData.oauth_token
          this._browserAuthenticationWithToken(oauthToken)
        })
        .catch(console.warn)
  },

  _browserAuthenticationWithToken (oauthToken) {
    this.state.facade.listenForDeepLinking().then((uri) => {
      let parsedURI = OauthHelper.getOauthTokenAndVerifierFromURLCallback(uri)
      this.state.facade.stopListeningForDeepLinking()

      if (parsedURI.oauth_token == null || parsedURI.oauth_token.length === 0) {
        // user canceled login
        return
      }

      this.state.twitterService.getAccessToken(parsedURI.oauth_token, parsedURI.oauth_verifier)
        .then((tokenData) => {
          let authService = this.state.authenticationService
          authService.updateWithTokenData(tokenData).then(() => {
            if (authService.isUserLoggedIn()) {
              this._pushTweetsList()
            } else {
              console.log('Something went wrong :o')
            }
          })
        })
    })
    Linking.openURL('https://api.twitter.com/oauth/authenticate?oauth_token=' + oauthToken)
  },

  _pushTweetsList () {
    this.props.navigator.resetTo({id: 'tweets-list-identifier'})
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  title: {
    fontSize: 20,
    textAlign: 'center'
  },
  button: {
    fontSize: 30,
    color: 'black'
  },
  button_disabled: {
    fontSize: 30,
    color: 'grey'
  }
})

module.exports = LoginScreenView
