const Rss = require('rss-parser')

const webTags = ["react", "javascript"]
const androidTags = ["android", "google"]
const iosTags = ["ios", "xcode", "swift", "apple"]

const extractDiversityFrom = (url) => {
    return parseRss(url).then(rss => {
        return rss.feed.entries;
    }).then(toViewState)
}

const parseRss = (url) => {
    return new Promise((resolve, reject) => {
        Rss.parseURL(url, (err, result) => {
            err ? reject(err) : resolve(result)
        })
    })
}

const toViewState = (items) => {
    var androidMentions=0
    var webMentions=0
    var iosMentions=0
    
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null){
            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()

                var isAndroidMention = androidTags.some(function(v){ return v.indexOf(category)>=0 })
                var isIosMention = iosTags.some(function(v){ return v.indexOf(category)>=0 })
                var isWebMention = webTags.some(function(v){ return v.indexOf(category)>=0 })

                if (isAndroidMention){
                    androidMentions++        
                }

                if (isIosMention){
                    iosMentions++        
                }

                if (isWebMention){
                    webMentions++        
                }
            }
        }
    }
        
    return {
        webMentions: webMentions,
        androidMentions: androidMentions,
        iosMentions: iosMentions,
    }
}

module.exports = () => extractDiversityFrom('https://www.novoda.com/blog/rss/')