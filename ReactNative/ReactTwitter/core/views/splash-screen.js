import React from 'react'
import {
  StyleSheet,
  View,
  Navigator,
  Image,
  Dimensions
} from 'react-native'
var AuthenticationService = require('../service/authentication-service.js')
var TwitterRequestsService = require('../service/twitter-requests-service.js')

const SPLASH_DURATION_MS = 1000

var SplashScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    let authService = new AuthenticationService()
    return {
      authenticationService: authService,
      twitterService: new TwitterRequestsService(authService)
    }
  },

  componentDidMount () {
    let authService = this.state.authenticationService

    authService.loadData()
      .then(() => {
        return new Promise((resolve, reject) => {
          setTimeout(resolve, SPLASH_DURATION_MS)
        })
      })
      .then(() => {
        if (authService.isUserLoggedIn()) {
          this._pushTweetsList()
        } else {
          this._pushLogin()
        }
      })
      .catch((error) => {
        console.warn(error)
        authService.logoutCurrentUser()
        this._pushLogin()
      })
  },

  render () {
    return (
      <View style={styles.container}>
        <Image style={styles.logo} source={require('./assets/twitter-logo.png')} />
      </View>
    )
  },

  _pushLogin () {
    this.props.navigator.resetTo({id: 'login-screen-identifier', twitterService: this.state.twitterService})
  },

  _pushTweetsList () {
    this.props.navigator.resetTo({id: 'tweets-list-identifier', twitterService: this.state.twitterService})
  }
})

var screenWidth = Dimensions.get('window').width
var screenHeight = Dimensions.get('window').height

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  logo: {
    flex: 1,
    width: screenWidth,
    height: screenHeight,
    resizeMode: 'contain'
  }
})

module.exports = SplashScreenView
