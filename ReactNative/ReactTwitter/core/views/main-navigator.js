import React from 'react'
import {
  Navigator
} from 'react-native'

var DebugScreenView = require('./debug-screen.js')
var DeepLinkingView = require('./deep-linking.js')
var OauthView = require('./oauth-screen.js')

const debugScreenID = 'debug-screen-identifier'
const deepLinkingID = 'deep-linking-identifier'
const OauthViewID = 'oauth-screen-identifier'

var MainNavigator = React.createClass({

  render () {
    return (
      <Navigator
        initialRoute={{id: debugScreenID}}
        renderScene={this.navigatorRenderScene}/>
    )
  },

  navigatorRenderScene (route, navigator) {
    switch (route.id) {
      case debugScreenID:
        return (<DebugScreenView navigator={navigator} title='Debug Screen' />)
      case deepLinkingID:
        return (<DeepLinkingView navigator={navigator} title='Deep Linking' />)
      case OauthViewID:
        return (<OauthView navigator={navigator} title='Oauth' />)
    }
  }
})

module.exports = MainNavigator
