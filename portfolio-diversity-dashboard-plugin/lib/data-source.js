const Rss = require('rss-parser')

const webTags = ["react", "javascript"]
const androidTags = ["android", "google", "kotlin"]
const iosTags = ["ios", "xcode", "swift", "apple"]

const extractMentionsFrom = (url) => {
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
    var mentions = new Map([["android", { count: 0 }], ["ios", { count: 0 }], ["web", { count: 0 }]])
    countMentions(mentions, items)
        
    return {
        webMentions: mentions.get("web").count,
        androidMentions: mentions.get("android").count,
        iosMentions: mentions.get("ios").count,
    }
}

function countMentions(mentions, items){
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null) {
            
            var copyAndroidTags = androidTags.slice()
            var copyIosTags = iosTags.slice()
            var copyWebTags = webTags.slice()
            
            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()                
                countMentionWhenTagsHaveCategory(mentions.get("android"), category, copyAndroidTags)
                countMentionWhenTagsHaveCategory(mentions.get("ios"), category, copyIosTags)
                countMentionWhenTagsHaveCategory(mentions.get("web"), category, copyWebTags)
            }
        }
    }
}

function countMentionWhenTagsHaveCategory(mention, category, tags){
    var catgoryIndex = findIndexForCategory(category, tags)
    if (catgoryIndex >=0){
        tags.splice(catgoryIndex, 1)
        mention.count = mention.count+1
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

module.exports = () => extractMentionsFrom('https://www.novoda.com/blog/rss/')