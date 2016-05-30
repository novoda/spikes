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
  text: PropTypes.string,
  onPress: PropTypes.func,
  ...View.propTypes // include the default view properties
}

module.exports = Button
