// @flow

import React from 'react'
import {
  StyleSheet,
  View,
  Text,
  TouchableHighlight,
  Navigator
} from 'react-native'

var NovodaDataFormat = require('./common/data-format.js')
var TwitterRequestsService = require('../service/twitter-requests-service.js')
var AvatarView = require('./common/avatar-view.js')

var TweetsListItem = React.createClass({
  propTypes: {
    id: React.PropTypes.string.isRequired,
    time: React.PropTypes.number.isRequired,
    author_avatar: React.PropTypes.string.isRequired,
    author_name: React.PropTypes.string.isRequired,
    author_handle: React.PropTypes.string.isRequired,
    text: React.PropTypes.string.isRequired,
    navigator: React.PropTypes.instanceOf(Navigator).isRequired,
    twitterService: React.PropTypes.instanceOf(TwitterRequestsService).isRequired
  },

  render () {
    let formattedTime = NovodaDataFormat.elapsedTime(this.props.time)
    let textWithoutUrls = this._removeUrls(this.props.text)
    return (
      <TouchableHighlight onPress={() => this._tweetSelected(this.props.id)}
        underlayColor='#dddddd'>
         <View>
          <View style={styles.rowContainer}>
            <AvatarView uri={this.props.author_avatar} size={60} />
            <View style={styles.textContainer}>
              <View style={styles.userInfoContainer}>
                <Text style={styles.tweet_author}>{this.props.author_name}</Text>
                <Text importantForAccessibility='no' style={styles.tweet_author_handle}>@{this.props.author_handle}</Text>
              </View>
              <Text style={styles.tweet_time}>{formattedTime}</Text>
              <Text style={styles.tweet_text} accessibilityLabel={textWithoutUrls}>{this.props.text}</Text>
            </View>
           </View>
           <View style={styles.separator}/>
         </View>
       </TouchableHighlight>
    )
  },

  _removeUrls (originalText: string) {
    // This is not the best user experience, accessibility-wise,
    // but we want to try different accessibilityLabels, for the sake of the spike
    return originalText.replace(/(?:https?|ftp):\/\/[\n\S]+/g, '')
  },

  _tweetSelected (tweetId: string) {
    this.props.navigator.push({
      id: 'tweet-view-identifier',
      tweetId: tweetId,
      twitterService: this.props.twitterService
    })
  }

})

const styles = StyleSheet.create({
  textContainer: {
    flex: 1,
    paddingLeft: 10
  },
  separator: {
    height: 1,
    backgroundColor: '#dddddd'
  },
  tweet_author: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'black'
  },
  tweet_author_handle: {
    fontSize: 16,
    fontStyle: 'italic',
    color: 'grey',
    marginLeft: 8
  },
  tweet_time: {
    fontSize: 16,
    color: '#656565'
  },
  tweet_text: {
    fontSize: 14,
    color: '#656565',
    marginTop: 8
  },
  rowContainer: {
    flexDirection: 'row',
    padding: 10,
    backgroundColor: 'white'
  },
  userInfoContainer: {
    flexDirection: 'row'
  }
})

module.exports = TweetsListItem
