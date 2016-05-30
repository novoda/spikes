// @flow

import React from 'react'
import {
  Navigator,
  StyleSheet,
  ListView,
  Alert
} from 'react-native'

var Button = require('../widgets/button.js')

var DebugNativeComponentScene = React.createClass({
  statics: {
    navigatorID () {
      return 'debug-native-component-identifier'
    },
    navigatorTitle () {
      return 'Native Components'
    }
  },

  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState: function () {
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    let rows = ['Rick', 'And', 'Morty']
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

  _renderRow (rowData: string) {
    let enabled = this._enabledForRowData(rowData)
    let textColor = this._colorForRowData(rowData)
    return <Button text={rowData} enabled={enabled} textColor={textColor} style={{height: 60, width: 160}}
      onPress={() => { enabled && Alert.alert('clicked', rowData) }}/>
  },

  _colorForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return '#0072bc'
      case 'And': return '#bc0072'
      case 'Morty': return '#72bc00'
      default: return '#000000'
    }
  },

  _enabledForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return true
      case 'And': return true
      case 'Morty': return false
      default: return true
    }
  }
})

const styles = StyleSheet.create({
  list: {
    backgroundColor: '#F5FCFF'
  },
  container: {
    alignItems: 'flex-start'
  },
  text: {
    fontSize: 20,
    color: 'black',
    margin: 8
  }
})

module.exports = DebugNativeComponentScene
