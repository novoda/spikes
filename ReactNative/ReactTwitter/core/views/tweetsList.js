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
    this._refreshData()
  }

  /*global fetch*/
  /*eslint no-undef: "error"*/
  _refreshData () {
    var userHandle = 'twitterapi'
    var url = 'https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=' + userHandle
    fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': 'OAuth oauth_consumer_key="aqPSTs1FT2ndP7qXi247BtbXd", oauth_nonce="987e33536527c50a7a0e1eb7b2d77e36", oauth_signature="K5W7yxsVPmQgd8arTIJ8qZXq%2FTk%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1463065323", oauth_token="730013266697654273-RDssoTCOdHNQtFA9k87OSijeZHcF4SU", oauth_version="1.0"'
      }
    })
      .then((response) => response.json())
      .then((rjson) => {
        console.log(rjson)
        this.setState({
          dataSource: this.state.dataSource.cloneWithRows(rjson)
        })
      })
      .done()
  }

  renderRow (rowData) {
    return (
       <TweetsListItem
          id={rowData.id_str}
          time={Date.parse(rowData.created_at)}
          author_avatar={rowData.user.profile_image_url}
          author_name={rowData.user.name}
          author_handle={rowData.user.screen_name}
          text={rowData.text}
          navigator={this.props.navigator}
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
