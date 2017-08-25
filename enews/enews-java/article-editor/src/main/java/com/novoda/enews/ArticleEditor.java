package com.novoda.enews;

import com.larvalabs.linkunfurl.LinkInfo;
import com.larvalabs.linkunfurl.LinkUnfurl;

import java.io.IOException;
import java.util.stream.Stream;

class ArticleEditor {

    Stream<Article> generateArticle(Stream<ChannelHistory.Message> messageStream) {
        return messageStream.parallel().map(this::generateArticle);
    }

    private Article generateArticle(ChannelHistory.Message message) {
        String imageUrl = generateImageUrl(message);
        String pageTitle = generateTitle(message);
        String text = generateText(message);
        String pageLink = message.getPageLink();
        return new Article(imageUrl, pageTitle, text, pageLink);
    }

    private String generateImageUrl(ChannelHistory.Message message) {
        if (message.hasImage()) {
            return message.getImageUrl();
        } else {
            return "https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/missing-image.png";
        }
    }

    private String generateText(ChannelHistory.Message message) {
        return message.getText()
                .replaceAll("<h", "h")
                .replaceAll("/>", "")
                .replaceAll(">", "")
                .replaceAll("\\?ref*[ ]", ""); // < TODO test
    }

    private String generateTitle(ChannelHistory.Message message) {
        try {
            LinkInfo info = LinkUnfurl.unfurl(message.getPageLink(), 3000);
            return info.getTitle();
        } catch (IOException e) {
            return "#eNews link";
        }
    }

}
