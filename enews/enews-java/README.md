The idea is to parse our #general channel for all posts that have the #eNews hashtag in a time range.

We then generate a newsletter from this feed.

Modules:
`slack-scraper` 
 - Returns a list of `ChannelHistory.Message`'s that all use the #eNews hashtag
 
 `article-editor`
 - This takes the `List<ChannelHistory.Message>` and converts them to a `Stream<Article>`. These articles are the "tidied up" version of the messages, including title previews. 
 
 `html-generator`
 - Creates a html newsletter from the `Stream<Article>`, i.e. a list of articles.
 
 `newsletter-publisher`
 - Talks to MailChimp to publish the html newsletter
 
 `aws-runner`
 - Takes all the above modules and creates an AWS lambda to run this newsletter in the _cloud_

License: Apache 2.0

Contributions welcome!
