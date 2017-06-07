export default class ActionResolver {

  constructor(tv) {
    this.tv = tv
  }

  async resolve({action, parameters, contexts}) {
    switch (action) {
    case 'tv_channel.now': {
      const time = parameters['date-time'] || 'now'
      const tvChannel = parameters['tv-channel'] || contexts.find(context => context.name === 'show').parameters['tv-channel']
      const shows = await this.tv.now()
      const showsOnDesiredChannel = shows.filter(item => item.type === time.toUpperCase())[0]
      const show = showsOnDesiredChannel.sliceItems.filter(item => item.slot.slotTXChannel === tvChannel)[0]
      return this.buildNowMessage(show)
    }
    case 'show.details': {
      const showName = parameters['tv-show'].toLowerCase().replace(/ /g, '-')
      const show = await this.tv.show(showName)
      return this.buildShowMessage(show)
    }
    default:
      return {}
    }
  }

  buildNowMessage(show) {
    return {
      speech: show.title,
      displayText: show.title,
      contextOut: [this.contextForShow(show, show.slot.slotTXDate)],
      data: this.cardsForShow(show)
    }
  }

  contextForShow(show, time) {
    return {name: 'show', lifespan: 5, parameters: {name: show.title, tv_channel: show.brand.presentationBrand, time}}
  }

  cardsForShow(show) {
    return {
      slack: this.cardSlackForShow(show),
      google: this.cardGoogleForShow(show)
    }
  }

  cardSlackForShow(show) {
    return {
      attachments: [
        {
          title: show.brand.title,
          text: show.brand.summary,
          image_url: show.brand.image.href
        }
      ]
    }
  }

  cardGoogleForShow(show) {
    return {
      expectUserResponse: true,
      richResponse: {
        items: [
          {
            simpleResponse: {
              textToSpeech: show.brand.title
            }
          },
          {
            basicCard: {
              title: show.brand.title,
              formattedText: show.brand.summary,
              image: {
                url: show.brand.image.href,
                accessibilityText: show.title + ' poster'
              }
            }
          }
        ]
      }
    }
  }

  buildShowMessage(show) {
    const episodes = show.brand.episodes
    return {
      speech: show.brand.summary,
      displayText: show.brand.summary,
      contextOut: [this.contextForShow(show, episodes[episodes.length - 1].firstTXDate)],
      data: this.cardsForShow(show)
    }
  }

}