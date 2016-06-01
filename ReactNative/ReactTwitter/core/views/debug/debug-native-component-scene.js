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
    let textSize = this._sizeForRowData(rowData)
    let backgroundImage = this._backgroundImageForRowData(rowData)

    return <Button text={rowData}
      enabled={enabled}
      textColor={textColor}
      textSize={textSize}
      backgroundImage={backgroundImage}
      style={{margin: 10}}
      onPress={() => { Alert.alert('clicked', rowData) }}/>
  },

  _colorForRowData (rowData: string) {
    switch (rowData) {
      case 'Rick': return '#ff00ff'
      case 'And': return '#000000'
      case 'Morty': return '#72bc00'
      default: return '#000000'
    }
  },

  _sizeForRowData (rowData: string) {
    switch (rowData) {
      case 'Rick': return 16
      case 'And': return 20
      case 'Morty': return 24
      default: return 14
    }
  },

  _enabledForRowData (rowData: string) {
    switch (rowData) {
      case 'Rick': return true
      case 'And': return true
      case 'Morty': return false
      default: return true
    }
  },

  _backgroundImageForRowData (rowData: string) {
    switch (rowData) {
      case 'Rick': return 'buttonBackground1'
      case 'And': return 'buttonBackground2'
      case 'Morty': return 'buttonBackground2'
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
