import React from 'react'
import {
  StyleSheet,
  View,
  Image,
  Text,
  Navigator
} from 'react-native'
import AndroidBackNavigationMixin from './mixins/android-back-navigation'
var novodaDataFormat = require('./common/data-format.js')

var TweetsView = React.createClass({
  mixins: [AndroidBackNavigationMixin],
  propTypes: {
    tweetId: React.PropTypes.string.isRequired,
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    return {
      tweet: {}
    }
  },

  componentDidMount () {
    this._refreshData()
  },

  /*global fetch*/
  /*eslint no-undef: "error"*/
  _refreshData () {
    var tweetId = this.props.tweetId
    var url = 'https://api.twitter.com/1.1/statuses/show.json?id=' + tweetId
    fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': 'OAuth oauth_consumer_key="aqPSTs1FT2ndP7qXi247BtbXd", oauth_nonce="9e48307c7ed7184557a3d88f6331f00f", oauth_signature="Jr9YqRlJ%2BNH%2BBgLC9cI%2F7LD06qg%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1463143567", oauth_token="730013266697654273-RDssoTCOdHNQtFA9k87OSijeZHcF4SU", oauth_version="1.0"'
      }
    })
      .then((response) => response.json())
      .then((rjson) => {
        this.setState({
          tweet: rjson
        })
      })
  },

  render () {
    if (!this.state.tweet.user) {
      return (<View />)
    }

    var formattedTime = novodaDataFormat.longTime(Date.parse(this.state.tweet.created_at))
    return (
      <View style={styles.mainContainer}>
        <View style={styles.header}>
          <Image style={styles.tweet_avatar} source={{ uri: this.state.tweet.user.profile_image_url }} />
          <View style={styles.textContainer}>
            <View>
              <Text style={styles.tweet_author}>{this.state.tweet.user.name}</Text>
              <Text style={styles.tweet_author_handle}>@{this.state.tweet.user.screen_name}</Text>
            </View>
          </View>
        </View>
        <Text style={styles.tweet_time}>{formattedTime}</Text>
        <Text style={styles.tweet_text}>{this.state.tweet.text}</Text>
      </View>
    )
  }
})

const styles = StyleSheet.create({
  textContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center'
  },
  tweet_author: {
    fontSize: 25,
    fontWeight: 'bold',
    color: '#48BBEC'
  },
  tweet_author_handle: {
    fontSize: 20,
    fontStyle: 'italic',
    color: '#656565'
  },
  tweet_time: {
    fontSize: 16,
    color: '#656565'
  },
  tweet_text: {
    fontSize: 20,
    color: '#656565'
  },
  tweet_avatar: {
    width: 80,
    height: 80,
    marginRight: 10
  },
  header: {
    flexDirection: 'row',
    marginBottom: 10
  },
  mainContainer: {
    padding: 10
  }
})

module.exports = TweetsView
