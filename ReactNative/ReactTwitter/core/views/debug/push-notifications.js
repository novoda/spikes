import React from 'react'
import {
  StyleSheet,
  View,
  Text
} from 'react-native'
var Button = require('react-native-button')
var NetworkCallsManager = require('../../service/network-calls-manager.js')
var PushNotificationService = require('../../service/push-notifications-service.js')

const PUSH_SERVER_URL = 'https://gcm-http.googleapis.com/gcm/send'
const PUSH_DELAY_MS = 2000
const PUSH_AUTHORIZATION_KEY = 'key=AIzaSyDiQB51T00e8s6JCkLYuvl0Ac05R0Dc4iY'

var PushNotificationsView = React.createClass({

  getInitialState () {
    return {
      token: ''
    }
  },

  componentDidMount () {
    PushNotificationService.getToken()
      .then((token) => {
        this.setState({token: token})
      })
  },

  render () {
    return (
      <View style={styles.container}>
        <Button
          style={styles.button}
          styleDisabled={styles.button_disabled}
          onPress={this._buttonClicked}> Send push notification</Button>
        <Text style={styles.normal}>
          The token is {"\n"}{this.state.token}
        </Text>
      </View>
    )
  },

  _buttonClicked () {
    let callsManager = new NetworkCallsManager()
    let body = JSON.stringify({
      to: this.state.token,
      data: {
        title: 'Rekt Native',
        message: 'New notification',
        data: {
          some: 'thing'
        }
      }
    })
    console.log(body)
    new Promise((resolve, reject) => {
      setTimeout(resolve, PUSH_DELAY_MS)
    })
    .then(() => {
      callsManager.makeCall(PUSH_SERVER_URL,
        {
          method: 'POST',
          headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
            Authorization: PUSH_AUTHORIZATION_KEY
          },
          body: body
        })
        .then((resonse) => {
          return resonse.json()
        })
        .then((rjson) => { console.log(rjson) })
    })
  }
})

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  normal: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
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

module.exports = PushNotificationsView
