// @flow

import React, { PropTypes } from 'react'
import {
  requireNativeComponent,
  View,
  TouchableWithoutFeedback
} from 'react-native'

var iface = {
  name: 'RCTNovodaButton',
  propTypes: {
    enabled: PropTypes.bool,
    text: PropTypes.string,
    textColor: PropTypes.string,
    textSize: PropTypes.number,
    backgroundImage: PropTypes.string,
    ...View.propTypes // include the default view properties
  }
}

var RCTNovodaButton = requireNativeComponent('RCTNovodaButton', iface)

class Button extends React.Component {

  render () {
    return (
      <TouchableWithoutFeedback onPress={() => { this._onPress() }}>
        <RCTNovodaButton {...this.props} />
      </TouchableWithoutFeedback>
    )
  }

  _onPress () {
    if (this.props.enabled) {
      this.props.onPress()
    }
  }
}

Button.propTypes = {
  enabled: PropTypes.bool,
  text: PropTypes.string,
  textColor: PropTypes.string,
  textSize: PropTypes.number,
  backgroundImage: PropTypes.string,
  onPress: PropTypes.func,
  ...View.propTypes // include the default view properties
}

module.exports = Button
