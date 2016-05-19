var ReactNative = require('react-native')
var {
  AsyncStorage
} = ReactNative

const TOKEN_STORAGE_KEY = '@AuthenticationServiceToken:key'

class AuthenticationService {
  loadData () {
    if (this.tokenData) {
      return new Promise((resolve, reject) => {
        resolve(this.tokenData)
      })
    }

    return AsyncStorage.getItem(TOKEN_STORAGE_KEY).then((tokenString) => {
      this.tokenData = JSON.parse(tokenString)
      return this.tokenData
    })
  }

  updateWithTokenData (tokenData) {
    this.tokenData = tokenData
    let tokenString = JSON.stringify(tokenData)
    return AsyncStorage.setItem(TOKEN_STORAGE_KEY, tokenString)
  }

  logoutCurrentUser () {
    this.tokenData = null
    return AsyncStorage.removeItem(TOKEN_STORAGE_KEY)
  }

  isUserLoggedIn () {
    return this.getOAuthToken().length > 0
  }

  getOAuthToken () {
    if (this.tokenData) {
      return this.tokenData.oauth_token
    }

    return ''
  }

  getSecretToken () {
    if (this.tokenData) {
      return this.tokenData.oauth_token_secret
    }

    return ''
  }

  getUsername () {
    if (this.tokenData) {
      return this.tokenData.screen_name
    }

    return ''
  }

  getUserID () {
    if (this.tokenData) {
      return this.tokenData.user_id
    }

    return ''
  }
}

module.exports = AuthenticationService
