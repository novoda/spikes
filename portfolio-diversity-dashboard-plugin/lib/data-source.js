const generateViewState = (configuration) => {
    console.log(configuration)    
    const viewState = {
        numWebMentions: 100,
        numAndroidMentions: 200,
        numIosMentions: 300,
    }
    return Promise.resolve(viewState)
}

module.exports = generateViewState
