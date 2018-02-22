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
    var mentions = new Map([["android", 0], ["ios", 0], ["web", 0]])
    mentions.set("android", counTagsIn(items, androidTags))
    mentions.set("ios", counTagsIn(items, iosTags))
    mentions.set("web", counTagsIn(items, webTags))
        
    return {
        mostUsedTags: mostUsedTagsFrom(items),
        webMentions: mentions.get("web"),
        androidMentions: mentions.get("android"),
        iosMentions: mentions.get("ios")
    }
}

function mostUsedTagsFrom(items){
    var tagsWithUsages = tagsWithUsagesFrom(items)
    console.log(tagsWithUsages);    
    return "android, swift, kotlin"
}

function tagsWithUsagesFrom(items){
    var tagsWithUsages = new Map()

    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null) {                

            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()                
                
                if (tagsWithUsages[category] == undefined){
                    tagsWithUsages[category] = 0
                } 
                
                tagsWithUsages[category] = tagsWithUsages[category]+1                
            }
        }
    }

    return tagsWithUsages
}

function counTagsIn(items, tags){
    var mentionCount = 0
    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null) {            
            var copyTags = tags.slice()
            
            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()                
                mentionCount+= countAndRemoveFoundTags(category, copyTags)
            }
        }
    }

    return mentionCount
}

function countAndRemoveFoundTags(category, tags){
    var count = 0
    var catgoryIndex = findIndexForCategoryIn(category, tags)
    if (catgoryIndex >=0){
        tags.splice(catgoryIndex, 1)
        count++
    }          
    return count         
}

function findIndexForCategoryIn(category, tags) {
    for (var i=tags.length; i--;) {
        var tag = tags[i].toLowerCase()
        if (tag.indexOf(category)>=0 || category.indexOf(tag)>=0){
            break;
        } 
    }
    return i
}

module.exports = () => extractMentionsFrom('https://www.novoda.com/blog/rss/')