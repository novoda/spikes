// @flow

import React from 'react'
import {
  StyleSheet,
  Navigator,
  ListView
} from 'react-native'

var Button = require('react-native-button')

var DebugScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState: function () {
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    let rows = [{
      name: 'Deep Linking',
      sceneID: 'deep-linking-identifier'
    }, {
      name: 'Oauth',
      sceneID: 'oauth-screen-identifier'
    }, {
      name: 'Push Notifications',
      sceneID: 'push-notifications-screen-identifier'
    }]
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
