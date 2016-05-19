import React from 'react'
import ReactNative from 'react-native'
const {
  NavigationExperimental,
  StyleSheet
} = ReactNative
const  {
  AnimatedView: NavigationAnimatedView,
  Card: NavigationCard,
  Header: NavigationHeader,
  Reducer: NavigationReducer,
} = NavigationExperimental;

var SplashScreenView = require('./splash-screen.js')
var DebugScreenView = require('./debug/debug-screen.js')
var LoginScreenView = require('./login-screen.js')
var DeepLinkingView = require('./debug/deep-linking.js')
var OauthView = require('./debug/oauth-screen.js')
var TweetsList = require('./tweetsList.js')
var TweetView = require('./tweetView.js')

const splashScreenID = 'splash-screen-identifier'
const loginScreenID = 'login-screen-identifier'
const debugScreenID = 'debug-screen-identifier'
const deepLinkingID = 'deep-linking-identifier'
const OauthViewID = 'oauth-screen-identifier'
const tweetsListID = 'tweets-list-identifier'
const tweetViewID = 'tweet-view-identifier'

const TwitterNavigatorReducer = NavigationReducer.StackReducer({
  getPushedReducerForAction: (action) => {
    if (action.type === 'push') {
      return {key: action.key}
    }
    return null;
  },
  getReducerForState: (initialState) => (state) => state || initialState,
  initialState: {
    key: splashScreenID,
    index: 0,
    children: [
      {key: splashScreenID},
    ],
  },
});

class MainNavigatorExperimental extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = TwitterNavigatorReducer();
  }

  render () {
    return (
      <NavigationAnimatedView
        navigationState={this.state}
        onNavigate={this._handleAction}
        style={styles.animatedView}
        renderOverlay={this._renderHeader}
        renderScene={this._renderCard}/>
    )
  }

  componentWillMount() {
    this._renderCard = this._renderCard.bind(this);
    // this._renderHeader = this._renderHeader.bind(this);
    this._navigatorRenderScene = this._navigatorRenderScene.bind(this);
    // this._renderTitleComponent = this._renderTitleComponent.bind(this);
    this._handleAction = this._handleAction.bind(this);
  }

  _handleAction(action): boolean {
    console.log(action)
    if (!action) {
      return false;
    }
    const newState = TwitterNavigatorReducer(this.state, action);
    if (newState === this.state) {
      return false;
    }
    this.setState(newState);
    return true;
  }

  _renderHeader(/*NavigationSceneRendererProps*/ props) {
    return (
      <NavigationHeader
        {...props}
        renderTitleComponent={this._renderTitleComponent}
      />
    );
  }


  _renderCard(/*NavigationSceneRendererProps*/ props) {
    return (
      <NavigationCard
        {...props}
        key={props.scene.navigationState.key}
        renderScene={this._navigatorRenderScene}
      />
    );
  }

  _renderTitleComponent(/*NavigationSceneRendererProps*/ props) {
  return (
    <NavigationHeader.Title>
      {props.scene.navigationState.key}
    </NavigationHeader.Title>
  );
}

  _navigatorRenderScene (props) {
    console.log(props)
    switch (props.navigationState.key) {
      case splashScreenID:
        return (<SplashScreenView navigator={navigator} title='Splash Screen' />)
      case loginScreenID:
        return (<LoginScreenView navigator={navigator} title='Login Screen' twitterService={props.twitterService}/>)
      case debugScreenID:
        return (<DebugScreenView navigator={navigator} title='Debug Screen' />)
      case deepLinkingID:
        return (<DeepLinkingView navigator={navigator} title='Deep Linking' />)
      case OauthViewID:
        return (<OauthView navigator={navigator} title='Oauth' />)
      case tweetsListID:
        return (<TweetsList navigator={navigator} title='Tweets List' twitterService={props.twitterService} />)
      case tweetViewID:
        return (<TweetView navigator={navigator} tweetId={props.tweetId} title='Tweet View' twitterService={props.twitterService} />)
    }
  }
}

const styles = StyleSheet.create({
  animatedView: {
    flex: 1,
  }
});


module.exports = MainNavigatorExperimental
