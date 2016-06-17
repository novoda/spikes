// @flow

import React from 'react'
import {
  ListView,
  Navigator,
  RefreshControl,
  StyleSheet
} from 'react-native'
import TweetsListItem from './tweetsListItem'
import AndroidBackNavigationMixin from './mixins/android-back-navigation'
var TwitterRequestsService = require('../service/twitter-requests-service.js')

var TweetsList = React.createClass({
  mixins: [AndroidBackNavigationMixin],
  statics: {
    navigatorID () {
      return 'tweets-list-identifier'
    },
    navigatorTitle () {
      return 'Timeline'
    }
  },
  propTypes: {
    twitterService: React.PropTypes.instanceOf(TwitterRequestsService).isRequired,
    navigator: React.PropTypes.instanceOf(Navigator).isRequired
  },

  getInitialState () {
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2})
    return {
      isRefreshing: false,
      dataSource: ds.cloneWithRows([])
    }
  },

  componentDidMount () {
    this._refreshData()
  },

  _refreshData () {
    this.setState({isRefreshing: true})
    this.props.twitterService.getHomeTimeline()
      .then((rjson) => {
        this.setState({
          dataSource: this.state.dataSource.cloneWithRows(rjson),
          isRefreshing: false
        })
      })
      .catch((err) => {
        console.warn(err)
        this.setState({isRefreshing: false})
      })
  },

  render () {
    return (
      <ListView
        dataSource={this.state.dataSource}
        enableEmptySections={true}
        renderRow={this.renderRow}
        refreshControl={this._refreshControl()}
        style={styles.container}
      />
    )
  },

  renderRow (rowData: any) {
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

  _refreshControl () {
    return (<RefreshControl
      refreshing={this.state.isRefreshing}
      onRefresh={this._refreshData}
      tintColor='#aaaaaa'
      title='Loading...'
      titleColor='#48BBEC'
      colors={['#48BBEC']}
      progressBackgroundColor='#ffffff'
    />)
  }
})

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white'
  }
})
module.exports = TweetsList
