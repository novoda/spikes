import request from 'request-promise-native'
import config from './config.js'

export default class TV {

  async fetch(result) {
    console.log(result)
    if (result.action === 'tv_channel.now') {
      const token = await this.token()
      const program = await this.now(token, result.parameters)
      return {
        speech: program.title,
        displayText: program.title,
        data: {
          slack: {
            attachments: [
              {
                title: program.title,
                text: program.brand.summary,
                image_url: program.brand.image.href
              }
            ]
          },
          google: {
            expectUserResponse: true,
            richResponse: {
              items: [
                {
                  simpleResponse: {
                    textToSpeech: program.title
                  }
                },
                {
                  basicCard: {
                    title: program.title,
                    formattedText: program.brand.summary,
                    image: {
                      url: program.brand.image.href,
                      accessibilityText: program.title + ' poster'
                    }
                  }
                }
              ]
            }
          }
        }
      }
    }
  }

  async token() {
    const payload = await request({
      method: 'POST',
      uri: config.urls.token,
      headers: {
        'Authorization': `Basic ${config.token}`
      },
      form: {
        grant_type: 'client_credentials'
      },
      json: true
    })
    return payload.access_token
  }

  async now(token, parameters) {
    const programmesOnAllChannel = await request({
      uri: config.urls.now,
      headers: {
        'Authorization': `Bearer ${token}`
      },
      json: true
    })
    const time = parameters['date-time'] || 'now'
    const programmesOnDesiredChannel = programmesOnAllChannel.filter(item => item.type === time.toUpperCase())[0]
    return programmesOnDesiredChannel.sliceItems.filter(item => item.slot.slotTXChannel === parameters['tv-channel'])[0]
  }

}
