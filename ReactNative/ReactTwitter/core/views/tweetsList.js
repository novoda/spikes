import React, { Component } from 'react'
import {
  Alert,
  StyleSheet,
  View,
  ListView,
  Text,
  TouchableHighlight
} from 'react-native'

class TweetsList extends Component {
  constructor (props) {
    super(props)
    var dataSource = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
      dataSource: dataSource.cloneWithRows([
        {
          id: '1',
          user: 'user 1',
          text: 'this is the first tweet'
        },
        {
          id: '2',
          user: 'user 2',
          text: 'this is the a cool tweet!!1!'
        },
        {
          id: '3',
          user: 'user 1',
          text: 'this is the second tweet'
        },
      ]),
    };
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
          <View style={styles.textContainer}>
            <Text>{rowData.user}</Text>
            <Text>{rowData.text}</Text>
          </View>
         </View>
         <View style={styles.separator}/>
       </View>
     </TouchableHighlight>
    );
  }

  render () {
    return (
      <ListView
        dataSource={this.state.dataSource}
        renderRow={this.renderRow.bind(this)}
      />
    );
  }
}

const styles = StyleSheet.create({
  thumb: {
    width: 80,
    height: 80,
    marginRight: 10
  },
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
  tweet_text: {
    fontSize: 20,
    color: '#656565'
  },
  rowContainer: {
    flexDirection: 'row',
    padding: 10
  }
})

module.exports = TweetsList
