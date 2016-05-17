import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  Navigator
} from 'react-native'
var AuthenticationService = require('../service/authentication-service.js')

const SPLASH_DURATION_MS = 1000

var SplashScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  componentDidMount () {
    let authService = new AuthenticationService()

    authService.loadDataFromDisk()
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
        <Text style={styles.title}>SplashScreen</Text>
      </View>
    )
  },

  _pushLogin () {
    this.props.navigator.resetTo({id: 'login-screen-identifier'})
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
    fontSize: 30,
    textAlign: 'center'
  }
})

module.exports = SplashScreenView
