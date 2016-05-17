import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  Navigator
} from 'react-native'

var Button = require('react-native-button')

var DebugScreenView = React.createClass({
  propTypes: {
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  render () {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Debug Screen List: </Text>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this.pushDeepLinking}>Deep Linking</Button>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this.pushTweetsList}>Tweets List</Button>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this.pushTweetView}>Tweet Detail View</Button>
      </View>
    )
  },

  pushDeepLinking () {
    this.props.navigator.push({id: 'deep-linking-identifier'})
  },

  pushTweetsList () {
    this.props.navigator.push({id: 'tweets-list-identifier'})
  },

  pushTweetView () {
    this.props.navigator.push({
      id: 'tweet-view-identifier',
      tweetId: '210462857140252672'
    })
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    backgroundColor: '#F5FCFF',
    margin: 10
  },
  title: {
    fontSize: 30,
    textAlign: 'center',
    marginTop: 20,
    marginBottom: 20
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

module.exports = DebugScreenView
