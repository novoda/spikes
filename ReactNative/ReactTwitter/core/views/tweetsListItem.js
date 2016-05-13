import React from 'react'
import {
  StyleSheet,
  View,
  Image,
  Text,
  TouchableHighlight,
  Navigator
} from 'react-native'

var dateFormat = require('dateformat')

var TweetsListItem = React.createClass({
  propTypes: {
    id: React.PropTypes.string.isRequired,
    time: React.PropTypes.number.isRequired,
    author_avatar: React.PropTypes.string.isRequired,
    author_name: React.PropTypes.string.isRequired,
    author_handle: React.PropTypes.string.isRequired,
    text: React.PropTypes.string.isRequired,
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  render () {
    var formattedTime = this._formatTime(this.props.time)
    return (
      <TouchableHighlight onPress={() => this._tweetSelected(this.props.id)}
        underlayColor='#dddddd'>
         <View>
          <View style={styles.rowContainer}>
            <Image style={styles.tweet_avatar} source={{ uri: this.props.author_avatar }} />
            <View style={styles.textContainer}>
              <Text style={styles.tweet_author}>{this.props.author_name}</Text>
              <Text style={styles.tweet_author_handle}>@{this.props.author_handle}</Text>
              <Text style={styles.tweet_time}>{formattedTime}</Text>
              <Text style={styles.tweet_text}>{this.props.text}</Text>
            </View>
           </View>
           <View style={styles.separator}/>
         </View>
       </TouchableHighlight>
    )
  },

  _formatTime (time) {
    return dateFormat(time, 'mmmm dS, yyyy, h:MM:ss TT')
  },

  _tweetSelected (tweetId) {
    this.props.navigator.push({
      id: 'tweet-view-identifier',
      tweetId: tweetId
    })
  }

})

const styles = StyleSheet.create({
  textContainer: {
    flex: 1
  },
  separator: {
    height: 1,
    backgroundColor: '#dddddd'
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
  rowContainer: {
    flexDirection: 'row',
    padding: 10
  }
})

module.exports = TweetsListItem
