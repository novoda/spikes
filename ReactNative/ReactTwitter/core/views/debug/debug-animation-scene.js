// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Navigator,
  Animated
} from 'react-native'

var Button = require('react-native-button')
const IMAGE_SIZE_NORMAL = 100
const IMAGE_SIZE_BIG = 200
const OPACITY_NORMAL = 1
const OPACITY_TRANSPARENT = 0
const DELAY = 50

var DebugAnimationScene = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    return {
      imageSize: new Animated.Value(IMAGE_SIZE_NORMAL),
      opacity: new Animated.Value(OPACITY_NORMAL)
    }
  },

  render () {
    return (
      <View style={styles.container}>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this._animateSpring}>Spring Animation</Button>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this._animateSequence}>Sequential Animations</Button>
        <Button style={styles.button} styleDisabled={styles.button_disabled} onPress={this._animateParallel}>Parallel Animations</Button>

        <Animated.Image source={require('../assets/twitter-logo.png')} style={{width: this.state.imageSize, height: this.state.imageSize, opacity: this.state.opacity, resizeMode: 'contain'}} />
      </View>
    )
  },

  _animateSpring () {
    Animated.spring(
      this.state.imageSize,
      {
        toValue: IMAGE_SIZE_BIG
      }
    ).start((finished) => {
      this._resetState()
    })
  },

  _animateSequence () {
    Animated.sequence([
      Animated.spring(
        this.state.imageSize,
        {
          toValue: IMAGE_SIZE_BIG
        }
      ),
      Animated.delay(DELAY),
      Animated.spring(
        this.state.imageSize,
        {
          toValue: IMAGE_SIZE_NORMAL
        }
      )
    ]).start((finished) => {
      this._resetState()
    })
  },

  _animateParallel () {
    Animated.parallel([
      Animated.spring(
        this.state.imageSize,
        {
          toValue: IMAGE_SIZE_BIG
        }
      ),
      Animated.spring(
        this.state.opacity,
        {
          toValue: OPACITY_TRANSPARENT
        }
      )
    ]).start((finished) => {
      this._resetState()
    })
  },

  _resetState () {
    this.state.imageSize.setValue(IMAGE_SIZE_NORMAL)
    this.state.opacity.setValue(OPACITY_NORMAL)
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
