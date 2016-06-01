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
    let props = this._propsForRowData(rowData)

    return <Button text={rowData}
      {...props}
      style={{margin: 10}}
      onPress={() => { Alert.alert('clicked', rowData) }}/>
  },

  _propsForRowData(rowData: string) {
    switch (rowData) {
      case 'Rick':
        return {
          enabled: true,
          textColor: '#ff00ff',
          textSize: 16,
          backgroundImage: 'buttonBackground1'

        }

      case 'And':
        return {
          enabled: true,
          textColor: '#000000',
          textSize: 20,
          backgroundImage: 'buttonBackground2'

        }

      case 'Morty':
        return {
          enabled: false,
          textColor: '#72bc00',
          textSize: 24,
          backgroundImage: 'buttonBackground2'

        }
      default: return { }
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
