import React from 'react'
import {
  Navigator,
  TouchableOpacity,
  StyleSheet,
  Image,
  Text
} from 'react-native'

var SplashScreenView = require('./splash-screen.js')
var DebugScreenView = require('./debug/debug-screen.js')
var LoginScreenView = require('./login-screen.js')
var DeepLinkingView = require('./debug/deep-linking.js')
var OauthView = require('./debug/oauth-screen.js')
var TweetsList = require('./tweetsList.js')
var TweetView = require('./tweetView.js')

const splashScreenID = 'splash-screen-identifier'
const loginScreenID = 'login-screen-identifier'
const debugScreenID = 'debug-screen-identifier'
const deepLinkingID = 'deep-linking-identifier'
const OauthViewID = 'oauth-screen-identifier'
const tweetsListID = 'tweets-list-identifier'
const tweetViewID = 'tweet-view-identifier'

var NavigationBarRouteMapper = {
  LeftButton (route, navigator, index, navState) {
    if (index === 0) {
      return null
    }

    return (
      <TouchableOpacity
        onPress={() => navigator.pop()}
        style={styles.navBarLeftButton}>
        <Text style={[styles.navBarText, styles.navBarButtonText]}>
          Back
        </Text>
      </TouchableOpacity>
    )
  },

  RightButton (route, navigator, index, navState) {
    if (index !== 0 || !__DEV__) { // eslint-disable-line no-undef
      return null
    }

    return (<TouchableOpacity onPress={() => navigator.push({id: debugScreenID})} style={styles.navBarRightButton}>
    <Text style={[styles.navBarText, styles.navBarButtonText]}>Debug</Text>
      </TouchableOpacity>)
  },

  Title (route, navigator, index, navState) {
    return (
      <Text style={[styles.navBarText, styles.navBarTitleText]}>
        {this._getTitleByScreenId(route.id)}
      </Text>
    )
  },

  _getTitleByScreenId (screenId) {
    switch (screenId) {
      case splashScreenID:
        return 'SplashScreen'
      case loginScreenID:
        return 'Login'
      case debugScreenID:
        return 'Debug Screen'
      case deepLinkingID:
        return 'Deep Linking'
      case OauthViewID:
        return 'Oauth'
      case tweetsListID:
        return 'Timeline'
      case tweetViewID:
        return 'Tweet Detail'
      default:
        return ''
    }
  }
}

var MainNavigator = React.createClass({
  render () {
    let navBar = (
      <Navigator.NavigationBar
        routeMapper={NavigationBarRouteMapper}
        style={styles.navBar}
      />)
    return (
      <Navigator
        style={styles.appContainer}
        navigationBar={navBar}
        initialRoute={{id: splashScreenID}}
        renderScene={this.navigatorRenderScene}
      />)
  },

  navigatorRenderScene (route, navigator) {
    switch (route.id) {
      case splashScreenID:
        return (<SplashScreenView navigator={navigator} />)
      case loginScreenID:
        return (<LoginScreenView navigator={navigator} twitterService={route.twitterService}/>)
      case debugScreenID:
        return (<DebugScreenView navigator={navigator} />)
      case deepLinkingID:
        return (<DeepLinkingView navigator={navigator} />)
      case OauthViewID:
        return (<OauthView navigator={navigator} />)
      case tweetsListID:
        return (<TweetsList navigator={navigator} twitterService={route.twitterService} />)
      case tweetViewID:
        return (<TweetView navigator={navigator} tweetId={route.tweetId} twitterService={route.twitterService} />)
    }
  }
})

const navBarIconSize = 30

var styles = StyleSheet.create({
  appContainer: {
    paddingTop: Navigator.NavigationBar.Styles.General.TotalNavHeight
  },
  navBarText: {
    fontSize: 16,
    marginVertical: 10
  },
  navBarLeftButton: {
    paddingLeft: 8
  },
  navBarRightButton: {
    // paddingBottom: (Navigator.NavigationBar.Styles.General.TotalNavHeight - navBarIconSize) / 2
    alignItems: 'center',
    paddingRight: 8
  },
  navBarButtonText: {
    color: 'black'
  },
  navBar: {
    backgroundColor: '#F1F1F1'
  },
  navBarIcon: {
    width: navBarIconSize,
    height: navBarIconSize
  }
})

module.exports = MainNavigator
