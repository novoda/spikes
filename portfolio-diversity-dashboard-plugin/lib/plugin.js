const plugin = require('dashboard-plugin')
const generateViewState = require('./data-source')

const component = {
    template: 'template.html',
    __dirname
}

const configuration = () => {
    return {
        "blog-diversity-dashboard-plugin": {
            name: 'Novoda Blog Diversity Dashboard'
        }
    }
}

module.exports = plugin.templated(configuration, component, generateViewState)
