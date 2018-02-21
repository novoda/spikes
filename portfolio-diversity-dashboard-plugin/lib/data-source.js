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
    var androidMentions=0
    var webMentions=0
    var iosMentions=0

    for (var itemIndex = 0; itemIndex < items.length; itemIndex++) {
        var item = items[itemIndex]
        if (item.categories != null){
            
            var copyAndroidTags = androidTags.slice()
            var copyIosTags = iosTags.slice()
            var copyWebTags = webTags.slice()
            
            for (var categoryIndex = 0; categoryIndex < item.categories.length; categoryIndex++){
                var category = item.categories[categoryIndex].toLowerCase()
                
                var androidCatgoryIndex = findIndexForCategory(category, copyAndroidTags)
                if (androidCatgoryIndex >=0){
                    copyAndroidTags.splice(androidCatgoryIndex, 1)
                    androidMentions++        
                }

                var iosCatgoryIndex = findIndexForCategory(category, copyIosTags)
                if (iosCatgoryIndex >=0){
                    copyIosTags.splice(iosCatgoryIndex, 1)
                    iosMentions++        
                }

                var webCatgoryIndex = findIndexForCategory(category, copyWebTags)
                if (webCatgoryIndex >=0){
                    copyWebTags.splice(webCatgoryIndex, 1)
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