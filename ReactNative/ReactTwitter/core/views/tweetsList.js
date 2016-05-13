import React, { Component } from 'react'
import { ListView } from 'react-native'
import TweetsListItem from './tweetsListItem'

class TweetsList extends Component {

  constructor (props) {
    super(props)
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    this.state = {
      dataSource: ds.cloneWithRows([])
    }
  }

  componentDidMount () {
    this.refreshData()
  }

  refreshData () {
    this.setState({
      dataSource: this.state.dataSource.cloneWithRows([
        {
          id: '1',
          created_at: 'Tue May 03 21:09:46 +0000 2016',
          user: {
            name: 'User 1',
            screen_name: 'user_1',
            profile_image_url: 'http://pbs.twimg.com/profile_images/2215576731/ars-logo_normal.png'
          },
          text: 'this is the first tweet'
        },
        {
          id: '2',
          created_at: 'Tue May 03 21:05:46 +0000 2016',
          user: {
            name: 'User 2',
            screen_name: 'another_user',
            profile_image_url: 'http://pbs.twimg.com/profile_images/655059892022022144/Pq3Q_1oU_normal.png'
          },
          text: 'this is the a cool tweet!!1!'
        },
        {
          id: '3',
          created_at: 'Tue May 03 20:55:46 +0000 2016',
          user: {
            name: 'User 1',
            screen_name: 'user_1',
            profile_image_url: 'http://pbs.twimg.com/profile_images/2215576731/ars-logo_normal.png'
          },
          text: 'this is the second tweet'
        }
      ])
    })
  }

  renderRow (rowData) {
    return (
       <TweetsListItem
          id={rowData.id}
          time={Date.parse(rowData.created_at)}
          author_avatar={rowData.user.profile_image_url}
          author_name={rowData.user.name}
          author_handle={rowData.user.screen_name}
          text={rowData.text}
        />
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

module.exports = TweetsList
