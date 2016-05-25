// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Navigator,
  Text,
  Animated
} from 'react-native'

var Button = require('react-native-button')

var DebugAnimationScene = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    return {
      imageSize: new Animated.Value(100),
      opacity: new Animated.Value(1)
    }
  },

  render () {
    return (
      <View style={styles.container}>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this.animateSpring}>Spring Animation</Button>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this.animateSequence}>Sequential Animations</Button>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this.animateParallel}>Parallel Animations</Button>

        <Animated.Image source={require('../assets/twitter-logo.png')} style={{width: this.state.imageSize, height: this.state.imageSize, opacity: this.state.opacity, resizeMode: 'contain'}} />
      </View>
    )
  },

  animateSpring () {
    Animated.spring(
      this.state.imageSize,
      {
        toValue: 200
      }
    ).start((finished) => {
      this._resetState()
    })
  },

  animateSequence () {
    Animated.sequence([
      Animated.spring(
        this.state.imageSize,
        {
          toValue: 200
        }
      ),
      Animated.delay(50),
      Animated.spring(
        this.state.imageSize,
        {
          toValue: 100
        }
      )
    ]).start((finished) => {
      this._resetState()
    })
  },

  animateParallel () {
    Animated.parallel([
      Animated.spring(
        this.state.imageSize,
        {
          toValue: 200
        }
      ),
      Animated.spring(
        this.state.opacity,
        {
          toValue: 0
        }
      )
    ]).start((finished) => {
      this._resetState()
    })
  },

  _resetState() {
    this.state.imageSize.setValue(100)
    this.state.opacity.setValue(1)
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
  }
})

module.exports = DebugAnimationScene
