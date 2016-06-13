// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Navigator,
  Text
} from 'react-native'

var Button = require('react-native-button')

var DebugUITestingScene = React.createClass({
  statics: {
    navigatorID () {
      return 'debug-uitesting-ios-scene-identifier'
    },
    navigatorTitle () {
      return 'iOS UI Testing'
    }
  },

  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    return {
      numberOfClicks: 0,
      text: 'No Clicks'
    }
  },

  render () {
    return (
      <View style={styles.container}>
        <Button style={styles.button}
          testID='button'
          styleDisabled={styles.button_disabled}
          onPress={this._updateText}>Update Label</Button>
        <Text style={styles.label} testID='label'>{this.state.text}</Text>
      </View>
    )
  },

  _updateText () {
    let newNumberOfClicks = this.state.numberOfClicks + 1
    this.setState({
      numberOfClicks: newNumberOfClicks,
      text: newNumberOfClicks + ' Clicks'
    })
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    backgroundColor: '#F5FCFF'
  },
  button: {
    fontSize: 20,
    color: 'black'
  },
  button_disabled: {
    fontSize: 20,
    color: 'grey'
  },
  label: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  }
})

module.exports = DebugUITestingScene
