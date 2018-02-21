const Rss = require('rss-parser')

const parseBlogPosts = (url) => {
    return parseRss(url).then(rss => {
        return rss.feed.entries;
    }).then(toItem)
}

const parseRss = (url) => {
    return new Promise((resolve, reject) => {
        Rss.parseURL(url, (err, result) => {
            err ? reject(err) : resolve(result)
        })
    })
}

const toItem = (items) => {
    return {
        numWebMentions: 100,
        numAndroidMentions: 200,
        numIosMentions: 300,
    }
}

module.exports = () => parseBlogPosts('https://www.novoda.com/blog/rss/')