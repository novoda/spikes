import React from 'react'
import { requireNativeComponent } from 'react-native'

var IOSButtonComponent = React.createClass({
  render () {
    return <RCTNovodaButton {...this.props} />
  },

  propTypes: {
    text: React.PropTypes.string
  }
})

var RCTNovodaButton = requireNativeComponent('RCTNovodaButton', IOSButtonComponent)
module.exports = RCTNovodaButton
