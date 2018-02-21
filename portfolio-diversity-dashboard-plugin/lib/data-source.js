const generateViewState = (configuration) => {
    const viewState = {
        foo: configuration.foo.value,
        bar: configuration.bar.value
    }
    return Promise.resolve(viewState)
}

module.exports = generateViewState
