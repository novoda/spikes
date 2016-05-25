// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Navigator,
  Image,
  Dimensions,
  Animated
} from 'react-native'
var AuthenticationService = require('../service/authentication-service.js')
var TwitterRequestsService = require('../service/twitter-requests-service.js')

const SPLASH_DELAY_DURATION_MS = 1000
const ANIMATION_DURATION_MS = 500

const SCREEN_WIDTH = Dimensions.get('window').width
const FINAL_LOGO_SIZE = SCREEN_WIDTH * 2

var SplashScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    let authService = new AuthenticationService()
    return {
      authenticationService: authService,
      twitterService: new TwitterRequestsService(authService),
      imageSize: new Animated.Value(SCREEN_WIDTH),
      opacity: new Animated.Value(1)
    }
  },

  componentDidMount () {
    let authService = this.state.authenticationService

    authService.loadData()
      .then(() => {
        return new Promise((resolve, reject) => {
          setTimeout(resolve, SPLASH_DELAY_DURATION_MS)
        })
      })
      .then(() => {
        this._animateLogo()
        return new Promise((resolve, reject) => {
          setTimeout(resolve, ANIMATION_DURATION_MS)
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
    let imageAnimatedStyle = {
      width: this.state.imageSize,
      height: this.state.imageSize,
      opacity: this.state.opacity
    }

    return (
      <View style={styles.container}>
        <Animated.Image style={[styles.logo, imageAnimatedStyle]} source={require('./assets/twitter-logo.png')} />
      </View>
    )
  },

  _pushLogin () {
    this.props.navigator.resetTo({id: 'login-screen-identifier', twitterService: this.state.twitterService})
  },

  _pushTweetsList () {
    this.props.navigator.resetTo({id: 'tweets-list-identifier', twitterService: this.state.twitterService})
  },

  _animateLogo () {
    Animated.parallel([
      Animated.timing(
        this.state.imageSize,
        {
          toValue: FINAL_LOGO_SIZE,
          duration: ANIMATION_DURATION_MS
        }
      ),
      Animated.timing(
        this.state.opacity,
        {
          toValue: 0,
          duration: ANIMATION_DURATION_MS
        }
      )
    ]).start()
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  logo: {
    flex: 1,
    resizeMode: 'contain'
  }
})

module.exports = SplashScreenView
