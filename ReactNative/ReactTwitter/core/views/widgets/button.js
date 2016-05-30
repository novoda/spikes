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
    text: PropTypes.string,
    enabled: PropTypes.bool,
    textColor: PropTypes.string,
    ...View.propTypes // include the default view properties
  }
}

var RCTNovodaButton = requireNativeComponent('RCTNovodaButton', iface)

class Button extends React.Component {

  render () {
    return (
      <TouchableWithoutFeedback onPress={this.props.onPress}>
        <RCTNovodaButton {...this.props} />
      </TouchableWithoutFeedback>
    )
  }
}

Button.propTypes = {
  enabled: PropTypes.bool,
  text: PropTypes.string,
  textColor: PropTypes.string,
  backgroundNormal: PropTypes.string,
  backgroundPressed: PropTypes.string,
  backgroundDisabled: PropTypes.string,
  onPress: PropTypes.func,
  ...View.propTypes // include the default view properties
}

module.exports = Button
