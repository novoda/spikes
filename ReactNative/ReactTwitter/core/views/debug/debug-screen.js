// @flow

import React from 'react'
import {
  StyleSheet,
  Navigator,
  ListView
} from 'react-native'

var Button = require('react-native-button')

var DeepLinkingView = require('./deep-linking.js')
var PushNotificationsScreenView = require('./push-notifications-screen.js')
var OauthView = require('./oauth-screen.js')
var DebugAnimationScene = require('./debug-animation-scene.js')
var DebugNativeComponentScene = require('./debug-native-component-scene.js')

const scenes = [
  DeepLinkingView,
  PushNotificationsScreenView,
  OauthView,
  DebugAnimationScene,
  DebugNativeComponentScene
]

var DebugScreenView = React.createClass({
  statics: {
    navigatorID () {
      return 'debug-screen-identifier'
    },
    navigatorTitle () {
      return 'Debug Screen'
    }
  },
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState: function () {
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    let rows = scenes.map((scene) => {
      return {
        name: scene.navigatorTitle(),
        sceneID: scene.navigatorID()
      }
    })

    return {
      dataSource: ds.cloneWithRows(rows)
    }
  },

  render () {
    return (
      <ListView
        dataSource={this.state.dataSource}
        enableEmptySections={true}
        renderRow={this._renderRow}
        style={styles.list}
        contentContainerStyle={styles.container}
      />
    )
  },

  _renderRow (rowData: any) {
    return <Button
      style={styles.button}
      styleDisabled={styles.button_disabled}
      onPress={() => this._navigateToScene(rowData.sceneID)}>{rowData.name}</Button>
  },

  _navigateToScene (sceneID: string) {
    this.props.navigator.push({id: sceneID})
  }
})

const styles = StyleSheet.create({
  list: {
    backgroundColor: '#F5FCFF'
  },
  container: {
    alignItems: 'flex-start'
  },
  button: {
    fontSize: 20,
    color: 'black',
    margin: 8
  },
  button_disabled: {
    fontSize: 20,
    color: 'grey'
  }
})

module.exports = DebugScreenView
