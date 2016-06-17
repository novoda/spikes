// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  Linking
} from 'react-native'

var Button = require('react-native-button')
var TwitterRequestsService = require('../../service/twitter-requests-service.js')
// $FlowFixMe: flow doesn't understand dynamic import
var DeepLinkingFacade = require('../../service/deep-linking-facade')
var OauthHelper = require('../../service/oauth-helper.js')
var AuthenticationService = require('../../service/authentication-service.js')

var OauthView = React.createClass({
  statics: {
    navigatorID () {
      return 'oauth-screen-identifier'
    },
    navigatorTitle () {
      return 'Oauth'
    }
  },

  getInitialState () {
    let authService = new AuthenticationService()
    return {
      accessToken: '',
      oauthVerifier: '',
      tokenData: null,
      asUserID: '',
      asToken: '',
      asSecretToken: '',
      asUsername: '',
      facade: new DeepLinkingFacade(),
      twitterService: new TwitterRequestsService(authService),
      authenticationService: authService
    }
  },

  componentDidMount () {
    this.state.authenticationService.loadData().then(() => {
      this._updateAuthenaticationServiceState()
    })
  },

  componentWillUnmount () {
    this.state.facade.stopListeningForDeepLinking()
  },

  render () {
    return (
      <View style={styles.container}>
      <Button
        style={styles.button}
        styleDisabled={styles.button_disabled}
        onPress={this._requestButtonClicked}> Request Access Token </Button>
        <Text style={styles.normal}>
          STEP 1 Access Token: {"\n"}{this.state.accessToken}{"\n"}{"\n"}
          STEP 2 Oauth Verifier: {"\n"}{this.state.oauthVerifier}{"\n"}{"\n"}
          STEP 3 Token Data: {"\n"}{JSON.stringify(this.state.tokenData)}
        </Text>

        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._updateAuthenticationButtonClicked}> Update Authentication Service From Response </Button>

          <Text style={styles.normal}>
            Authentication Service: {"\n"}
            user_id: {this.state.asUserID}{"\n"}
            token: {this.state.asToken}{"\n"}
            secret: {this.state.asSecretToken}{"\n"}
            username: {this.state.asUsername}{"\n"}
          </Text>
      </View>
    )
  },

  _requestButtonClicked () {
    this._resetOauthTokenData()
    this.state.twitterService.requestToken()
        .then((tokenData) => {
          let oauthToken = tokenData.oauth_token
          this.setState({accessToken: oauthToken})
          this._browserAuthenticationWithToken(oauthToken)
        })
        .catch(console.warn)
  },

  _browserAuthenticationWithToken (oauthToken: string) {
    this.state.facade.listenForDeepLinking().then((uri) => {
      let parsedURI: any = OauthHelper.getOauthTokenAndVerifierFromURLCallback(uri)
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
  },

  _resetOauthTokenData () {
    this.setState({
      accessToken: '',
      oauthVerifier: '',
      tokenData: null
    })
  },

  _updateAuthenticationButtonClicked () {
    this._updateAuthenticationServiceWithCurrentTokenData()
  },

  _updateAuthenticationServiceWithCurrentTokenData () {
    let authService = this.state.authenticationService
    if (this.state.tokenData != null) {
      authService.updateWithTokenData(this.state.tokenData).then(() => {
        this._updateAuthenaticationServiceState()
      })
    } else {
      this._updateAuthenaticationServiceState()
    }
  },

  _updateAuthenaticationServiceState () {
    let authService = this.state.authenticationService
    this.setState({
      asUserID: authService.getUserID(),
      asToken: authService.getOAuthToken(),
      asSecretToken: authService.getSecretToken(),
      asUsername: authService.getUsername()
    })
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
    color: 'black',
    margin: 10
  },
  button_disabled: {
    fontSize: 20,
    color: 'grey',
    margin: 10
  }
})

module.exports = OauthView
