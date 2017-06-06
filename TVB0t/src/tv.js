import request from 'request-promise-native'

export default class TV {

  constructor(config) {
    this.config = config
  }

  async fetch(time, tvChannel) {
    const token = await this.token()
    return await this.now(token, time, tvChannel)
  }

  async token() {
    const payload = await request({
      method: 'POST',
      uri: this.config.urls.token,
      headers: {
        'Authorization': `Basic ${this.config.token}`
      },
      form: {
        grant_type: 'client_credentials'
      },
      json: true
    })
    return payload.access_token
  }

  async now(token, time, tvChannel) {
    const programmesOnAllChannel = await request({
      uri: this.config.urls.now,
      headers: {
        'Authorization': `Bearer ${token}`
      },
      json: true
    })
    const programmesOnDesiredChannel = programmesOnAllChannel.filter(item => item.type === time.toUpperCase())[0]
    return programmesOnDesiredChannel.sliceItems.filter(item => item.slot.slotTXChannel === tvChannel)[0]
  }

}
