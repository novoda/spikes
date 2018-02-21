const Rss = require('rss-parser')

const webTags = ["react", "javascript"]
const androidTags = ["android", "google", "kotlin"]
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
    var mentions = new Map([["android", 0], ["ios", 0], ["web", 0]])
    
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null){
            
            var copyAndroidTags = androidTags.slice()
            var copyIosTags = iosTags.slice()
            var copyWebTags = webTags.slice()
            
            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()                
                countMentionsFor(mentions, "android", category, copyAndroidTags)
                countMentionsFor(mentions, "ios", category, copyIosTags)
                countMentionsFor(mentions, "web", category, copyWebTags)
            }
        }
    }
        
    return {
        webMentions: mentions.get("web"),
        androidMentions: mentions.get("android"),
        iosMentions: mentions.get("ios"),
    }
}

function countMentionsFor(mentions, platform, category, tags){
    var catgoryIndex = findIndexForCategory(category, tags)
    if (catgoryIndex >=0){
        tags.splice(catgoryIndex, 1)
        mentions.set(platform,mentions.get(platform)+1)        
    }                   
}

function findIndexForCategory(category, tags) {
    for (var i=tags.length; i--;) {
        var tag = tags[i].toLowerCase()
        if (tag.indexOf(category)>=0 || category.indexOf(tag)>=0){
            break;
        } 
    }
    return i
}

module.exports = () => extractDiversityFrom('https://www.novoda.com/blog/rss/')