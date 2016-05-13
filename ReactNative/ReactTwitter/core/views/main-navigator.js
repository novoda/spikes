import React from 'react'
import {
  Navigator
} from 'react-native'

var SplashScreenView = require('./splash-screen.js')
var DebugScreenView = require('./debug-screen.js')
var DeepLinkingView = require('./deep-linking.js')
var OauthView = require('./oauth-screen.js')
var TweetsList = require('./tweetsList.js')

const splashScreenID = 'splash-screen-identifier'
const debugScreenID = 'debug-screen-identifier'
const deepLinkingID = 'deep-linking-identifier'
const OauthViewID = 'oauth-screen-identifier'
const tweetsListID = 'tweets-list-identifier'

var MainNavigator = React.createClass({

  render () {
    return (
      <Navigator
        initialRoute={{id: splashScreenID}}
        renderScene={this.navigatorRenderScene}/>
    )
  },

  navigatorRenderScene (route, navigator) {
    switch (route.id) {
      case splashScreenID:
        return (<SplashScreenView navigator={navigator} title='Splash Screen' />)
      case debugScreenID:
        return (<DebugScreenView navigator={navigator} title='Debug Screen' />)
      case deepLinkingID:
        return (<DeepLinkingView navigator={navigator} title='Deep Linking' />)
      case OauthViewID:
        return (<OauthView navigator={navigator} title='Oauth' />)
      case tweetsListID:
        return (<TweetsList navigator={navigator} title='Tweets List' />)
    }
  }
})

module.exports = MainNavigator
