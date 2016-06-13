// @flow

import React from 'react'
import {
  Navigator,
  TouchableOpacity,
  StyleSheet,
  Text
} from 'react-native'

var SplashScreenView = require('./splash-screen.js')
var DebugScreenView = require('./debug/debug-screen.js')
var LoginScreenView = require('./login-screen.js')
var DeepLinkingView = require('./debug/deep-linking.js')
var PushNotificationsScreenView = require('./debug/push-notifications-screen.js')
var OauthView = require('./debug/oauth-screen.js')
var TweetsList = require('./tweetsList.js')
var TweetView = require('./tweetView.js')
var DebugAnimationScene = require('./debug/debug-animation-scene.js')
var DebugNativeComponentScene = require('./debug/debug-native-component-scene.js')
var DebugUITestingScene = require('./debug/debug-uitesting.js')

const scenes = [
  SplashScreenView,
  DebugScreenView,
  LoginScreenView,
  DeepLinkingView,
  PushNotificationsScreenView,
  OauthView,
  TweetsList,
  TweetView,
  DebugAnimationScene,
  DebugNativeComponentScene,
  DebugUITestingScene
]

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

    return (<TouchableOpacity testID='debug' onPress={() => navigator.push({id: DebugScreenView.navigatorID()})} style={styles.navBarRightButton}>
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
    for (let scene of scenes) {
      if (scene.navigatorID() === screenId) {
        return scene.navigatorTitle()
      }
    }

    return ''
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
        initialRoute={{id: SplashScreenView.navigatorID()}}
        renderScene={this.navigatorRenderScene}
      />)
  },

  navigatorRenderScene (route: any, navigator: Navigator) {
    switch (route.id) {
      case SplashScreenView.navigatorID():
        return (<SplashScreenView navigator={navigator} />)
      case LoginScreenView.navigatorID():
        return (<LoginScreenView navigator={navigator} twitterService={route.twitterService}/>)
      case DebugScreenView.navigatorID():
        return (<DebugScreenView navigator={navigator} />)
      case DeepLinkingView.navigatorID():
        return (<DeepLinkingView navigator={navigator} />)
      case PushNotificationsScreenView.navigatorID():
        return (<PushNotificationsScreenView />)
      case OauthView.navigatorID():
        return (<OauthView navigator={navigator} />)
      case TweetsList.navigatorID():
        return (<TweetsList navigator={navigator} twitterService={route.twitterService} />)
      case TweetView.navigatorID():
        return (<TweetView navigator={navigator} tweetId={route.tweetId} twitterService={route.twitterService} />)
      case DebugAnimationScene.navigatorID():
        return (<DebugAnimationScene navigator={navigator} />)
      case DebugNativeComponentScene.navigatorID():
        return (<DebugNativeComponentScene navigator={navigator} />)
      case DebugUITestingScene.navigatorID():
        return (<DebugUITestingScene navigator={navigator} />)
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
