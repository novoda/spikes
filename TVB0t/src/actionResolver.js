export default class ActionResolver {

  constructor(tv) {
    this.tv = tv
  }

  async resolve({action, parameters, contexts}) {
    switch (action) {
    case 'tv_channel.now': {
      const time = parameters['date-time'] || 'now'
      const tvChannel = parameters['tv-channel'] || contexts.find(context => context.name === 'show').parameters['tv-channel']
      const program = await this.tv.fetch(time, tvChannel)
      return this.buildNowMessage(program)
    }
    default:
      return {}
    }
  }

  buildNowMessage(program) {
    return {
      speech: program.title,
      displayText: program.title,
      contextOut: [{name: 'show', lifespan: 5, parameters: {name: program.title, tv_channel: program.slot.slotTXChannel}}],
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