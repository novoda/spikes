import React from 'react'
import { BackAndroid, Navigator } from 'react-native'

/*eslint-disable no-unused-vars*/
var AndroidBackNavigationMixin = {
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  componentDidMount () {
    BackAndroid.addEventListener('hardwareBackPress', () => {
      if (this.props.navigator.getCurrentRoutes().length === 1) {
        return false
      }
      this.props.navigator.pop()
      return true
    })
  },

  componentWillUnmount: function () {
    BackAndroid.removeEventListener('hardwareBackPress')
  }
}

module.exports = AndroidBackNavigationMixin
