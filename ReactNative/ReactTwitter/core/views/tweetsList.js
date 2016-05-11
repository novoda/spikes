import React, { Component } from 'react'
import {
  Alert,
  StyleSheet,
  View,
  Image,
  ListView,
  Text,
  TouchableHighlight
} from 'react-native'

class TweetsList extends Component {

  constructor (props) {
    super(props)
    var dataSource = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    this.state = {
      dataSource: dataSource.cloneWithRows([
        {
          id: '1',
          user: {
            name: 'User 1',
            screen_name: 'user_1',
            profile_image_url: 'http://pbs.twimg.com/profile_images/2215576731/ars-logo_normal.png'
          },
          text: 'this is the first tweet'
        },
        {
          id: '2',
          user: {
            name: 'User 2',
            screen_name: 'another_user',
            profile_image_url: 'http://pbs.twimg.com/profile_images/655059892022022144/Pq3Q_1oU_normal.png'
          },
          text: 'this is the a cool tweet!!1!'
        },
        {
          id: '3',
          user: {
            name: 'User 1',
            screen_name: 'user_1',
            profile_image_url: 'http://pbs.twimg.com/profile_images/2215576731/ars-logo_normal.png'
          },
          text: 'this is the second tweet'
        }
      ])
    }
  }

  tweetSelected (tweetId) {
    // TODO: navigate to tweet detail screen
    Alert.alert(
      'Tweet selected',
      'User selected tweet with id=' + tweetId
    )
  }

  renderRow (rowData, sectionID, rowID) {
    return (
      <TouchableHighlight onPress={() => this.tweetSelected(rowData.id)}
         underlayColor='#dddddd'>
       <View>
        <View style={styles.rowContainer}>
          <Image style={styles.tweet_avatar} source={{ uri: rowData.user.profile_image_url }} />
          <View style={styles.textContainer}>
            <Text style={styles.tweet_author}>{rowData.user.name}</Text>
            <Text style={styles.tweet_author_handle}>{rowData.user.screen_name}</Text>
            <Text style={styles.tweet_text}>{rowData.text}</Text>
          </View>
         </View>
         <View style={styles.separator}/>
       </View>
     </TouchableHighlight>
    )
  }

  render () {
    return (
      <ListView
        dataSource={this.state.dataSource}
        renderRow={this.renderRow.bind(this)}
      />
    )
  }
}

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

module.exports = TweetsList
