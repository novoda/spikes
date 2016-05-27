// @flow

import React, { PropTypes } from 'react'
import {
  requireNativeComponent,
  View,
  TouchableWithoutFeedback
} from 'react-native'

var iface = {
  name: 'RCTButton',
  propTypes: {
    text: PropTypes.string,
    ...View.propTypes // include the default view properties
  },
};

var RCTButton = requireNativeComponent('RCTButton', iface)

class Button extends React.Component {

  render () {
    return (
      <TouchableWithoutFeedback onPress={this.props.onPress}>
        <RCTButton {...this.props} />
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
