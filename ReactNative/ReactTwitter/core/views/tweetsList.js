import React from 'react'
import {
  ListView,
  Navigator
} from 'react-native'
import TweetsListItem from './tweetsListItem'
import AndroidBackNavigationMixin from './mixins/android-back-navigation'
var TwitterRequestsService = require('../service/twitter-requests-service.js')

var TweetsList = React.createClass({
  mixins: [AndroidBackNavigationMixin],
  propTypes: {
    twitterService: React.PropTypes.instanceOf(TwitterRequestsService).isRequired,
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    return {
      dataSource: ds.cloneWithRows([])
    }
  },

  componentDidMount () {
    this._refreshData()
  },

  _refreshData () {
    this.props.twitterService.getHomeTimeline()
      .then((rjson) => {
        this.setState({
          dataSource: this.state.dataSource.cloneWithRows(rjson)
        })
      })
      .catch((err) => { console.log(err) })
  },

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
          twitterService={this.props.twitterService}
        />
    )
  },

  render () {
    return (
      <ListView
        dataSource={this.state.dataSource}
        enableEmptySections={true}
        renderRow={this.renderRow}
      />
    )
  }
})

module.exports = TweetsList
