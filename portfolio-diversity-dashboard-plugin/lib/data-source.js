const Rss = require('rss-parser')

const crossPlatformTags = ["react native", "flutter", "j2objc", "kotlin multiplatform", "kotlin/native"]
const androidTags = ["android", "google", "kotlin", "android studio", "gradle"]
const iosTags = ["ios", "xcode", "swift", "apple", "cocoa pods"]

const generateViewStateFrom = (url) => {
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
    var mentions = new Map([["android", 0], ["ios", 0], ["cross-platform", 0]])
    mentions.set("android", counUniqueTagsIn(items, androidTags))
    mentions.set("ios", counUniqueTagsIn(items, iosTags))
    mentions.set("cross-platform", counUniqueTagsIn(items, crossPlatformTags))
        
    return {
        mostUsedTags: mostUsedTagsFrom(items),
        crossPlatformMentions: mentions.get("cross-platform"),
        androidMentions: mentions.get("android"),
        iosMentions: mentions.get("ios")
    }
}

function mostUsedTagsFrom(items){
    return sortMapDescendingByValue(tagsWithUsagesFrom(items))
        .splice(0,3)
        .map(tagUsage => tagUsage[0])
        .join(", ")
}

function tagsWithUsagesFrom(items){
    var tagsWithUsages = new Map()

    items
        .filter(item => item.categories != null)
        .map(function(item){ return item.categories})
        .map(function(categories){
            categories.forEach(category => {
                if (tagsWithUsages[category] == undefined){
                    tagsWithUsages[category] = 0
                } 
                
                tagsWithUsages[category] = tagsWithUsages[category]+1                 
            });
        })

    return tagsWithUsages
}

function sortMapDescendingByValue(obj)
{
	var sortable=[];
	for(var key in obj)
		if(obj.hasOwnProperty(key))
			sortable.push([key, obj[key]]);
	
	sortable.sort(function(a, b)
	{
		var x=a[1],
			y=b[1];
		return y<x ? -1 : y>x ? 1 : 0;
    });
    
    return sortable;
}

function counUniqueTagsIn(items, tags){
    var mentionCount = 0

    items
        .filter(item => item.categories != null)
        .map(function(item){ return item.categories})
        .map(function(categories){
            var copyTags = tags.slice()
            categories.forEach(element => {
                var category = element.toLowerCase()                
                mentionCount+= countAndRemoveFoundTags(category, copyTags)
            });
        })

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

module.exports = () => generateViewStateFrom('https://www.novoda.com/blog/rss/')