import moment from 'moment'

export default class ActionResolver {

  constructor(tv) {
    this.tv = tv
  }

  async resolve({action, parameters, contexts}) {
    switch (action) {
    case 'tv_channel.now': {
      const time = parameters['date-time'] || 'now'
      const tvChannel = parameters['tv-channel'] || this.extractParametersForContext('show', contexts)['tv-channel']
      const shows = await this.tv.now()
      const showsOnDesiredChannel = shows.filter(item => item.type === time.toUpperCase())[0]
      const show = showsOnDesiredChannel.sliceItems.filter(item => item.slot.slotTXChannel === tvChannel)[0]
      return this.buildNowMessage(show)
    }
    case 'show.details': {
      const showName = this.toWebSafeShowName(parameters['tv-show'])
      const show = await this.tv.show(showName)
      return this.buildShowMessage(show)
    }
    case 'recommendation.whatsnew': {
      const homepage = await this.tv.homepage()
      const recommendations = homepage.sliceGroups[0].slices.find(slice => slice.type === 'RECOMMENDATIONS').sliceItems
      return this.buildRecommendationMessage(recommendations.slice(0, 3))
    }
    case 'show.time-starting': {
      const tvShow = parameters['tv-show']
      let time = this.extractParametersForContext('show', contexts).time
      if (!time) {
        const showName = this.toWebSafeShowName(tvShow)
        const show = await this.tv.show(showName)
        const episodes = show.brand.episodes
        time = episodes[episodes.length - 1].firstTXDate
      }
      const message = tvShow + ' starts at ' + moment(time).format('h:mm a dddd, MMMM Do')
      const context = this.extractContext('show', contexts)
      context.parameters.time = time
      return {
        speech: message,
        displayText: message,
        contextOut: [context]
      }
    }
    case 'show.addtomylist': {
      const tvShow = parameters['tv-show'] || this.extractParametersForContext('show', contexts)['name']
      const showName = this.toWebSafeShowName(tvShow)
      let message
      try {
        const response = await this.tv.addToMyList(showName)
        console.log(response)
        message = tvShow + ' added to your list!'
      } catch (e) {
        console.log(e)
        message = 'Oops, something went wrong while trying to add ' + tvShow + ' to your list'
      }
      return {
        speech: message,
        displayText: message
      }
    }
    default: {
      const message = 'Oops this is not handled yet!'
      return {
        speech: message,
        displayText: message
      }
    }
    }
  }

  extractParametersForContext(contextName, contexts) {
    return this.extractContext(contextName, contexts).parameters
  }

  extractContext(contextName, contexts) {
    return contexts.find(context => context.name === contextName)
  }

  toWebSafeShowName(showName) {
    return showName.toLowerCase().replace(/ /g, '-')
  }

  buildNowMessage(show) {
    return {
      speech: show.title,
      displayText: show.title,
      contextOut: [this.contextForShow(show, show.slot.slotTXDate)],
      data: this.cardsForShow(show, show.brand.title)
    }
  }

  contextForShow(show, time) {
    return {
      name: 'show',
      lifespan: 5,
      parameters: {
        name: show.brand.title,
        tv_channel: show.brand.presentationBrand,
        time
      }
    }
  }

  cardsForShow(show, textToSpeech) {
    return {
      slack: this.cardSlackForShow(show),
      google: this.cardGoogleForShow(show, textToSpeech)
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

  cardGoogleForShow(show, textToSpeech) {
    return {
      expectUserResponse: true,
      richResponse: {
        items: [
          {
            simpleResponse: {
              textToSpeech: textToSpeech
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
      data: this.cardsForShow(show, show.brand.summary)
    }
  }

  buildRecommendationMessage(recommendations) {
    const recommendationList = recommendations
      .map(recommendation => recommendation.title)
      .reduce((accumulator, currentValue, currentIndex, array) => {
        if (accumulator.length === 0) {
          return currentValue
        }
        return accumulator + (currentIndex === array.length - 1 ? ' or ' : ', ') + currentValue
      })
    const message = 'I recommend you to watch ' + recommendationList
    return {
      speech: message,
      displayText: message,
      contextOut: [],
      data: {}
    }
  }

}