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
    let backgroundNormal = this._backgroundNormalForRowData(rowData)
    let backgroundPressed = this._backgroundPressedForRowData(rowData)
    let backgroundDisabled =  this._backgroundDisabledForRowData(rowData)

    return <Button text={rowData}
      enabled={enabled}
      textColor={textColor}
      backgroundNormal={backgroundNormal}
      backgroundPressed={backgroundPressed}
      backgroundDisabled={backgroundDisabled}
      style={{height: 60, width: 160, margin: 10}}
      onPress={() => { enabled && Alert.alert('clicked', rowData) }}/>
  },

  _colorForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return '#ff00ff'
      case 'And': return '#000000'
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
  },

  _backgroundNormalForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return 'button-background-normal1'
      case 'And': return 'button-background-normal2'
      case 'Morty': return 'button-background-normal2'
      default: return true
    }
  },

  _backgroundPressedForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return 'button-background-highlited1'
      case 'And': return 'button-background-highlited2'
      case 'Morty': return 'button-background-highlited2'
      default: return true
    }
  },

  _backgroundDisabledForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick': return 'button-background-disabled1'
      case 'And': return 'button-background-disabled2'
      case 'Morty': return 'button-background-disabled2'
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
