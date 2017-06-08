import request from 'request-promise-native'

export default class TV {

  constructor(config) {
    this.config = config
  }

  async token() {
    const payload = await request({
      method: 'POST',
      uri: this.url(this.config.paths.token),
      headers: {
        'Authorization': `Basic ${this.config.token}`
      },
      form: {
        grant_type: 'password',
        username: this.config.email,
        password: this.config.password
      },
      json: true
    })
    return payload.access_token
  }

  url(path) {
    return this.config.endpoint + path
  }

  async now() {
    const token = await this.token()
    return await request({
      uri: this.url(this.config.paths.now),
      headers: {
        'Authorization': `Bearer ${token}`
      },
      json: true
    })
  }

  async show(show) {
    const token = await this.token()
    return await request({
      uri: this.url(this.config.paths.show).replace('{{show}}', show),
      headers: {
        'Authorization': `Bearer ${token}`
      },
      json: true
    })
  }

  async homepage() {
    const token = await this.token()
    return await request({
      uri: this.url(this.config.paths.homepage),
      headers: {
        'Authorization': `Bearer ${token}`
      },
      json: true
    })
  }

}
