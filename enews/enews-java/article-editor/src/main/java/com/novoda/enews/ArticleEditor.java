package com.novoda.enews;

import com.novoda.enews.LinkUnfurler.UnsupportedUrlTypeException;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

public class ArticleEditor {

    private final LinkUnfurler linkUnfurler;
    private final MissingImageFetcher missingImageFetcher;

    public ArticleEditor(LinkUnfurler linkUnfurler, MissingImageFetcher missingImageFetcher) {
        this.linkUnfurler = linkUnfurler;
        this.missingImageFetcher = missingImageFetcher;
    }

    public Stream<Article> generateArticle(Stream<ChannelHistory.Message> messageStream) {
        return messageStream.parallel().map(this::generateArticle);
    }

    private Article generateArticle(ChannelHistory.Message message) {
        String imageUrl = generateImageUrl(message);
        String pageTitle = generateTitle(message);
        String text = generateText(message);
        String pageLink = generatePageLink(message);
        return new Article(imageUrl, pageTitle, text, pageLink);
    }

    private String generateImageUrl(ChannelHistory.Message message) {
        if (message.hasImage()) {
            return message.getImageUrl();
        } else {
            return missingImageFetcher.getMissingImageUrl();
        }
    }

    private String generateText(ChannelHistory.Message message) {
        String text = message.getText()
                .replaceAll("<h", "h")
                .replaceAll("/>", "")
                .replaceAll(">", "")
                .replaceAll("\\?(.*)", "")
                .replaceAll("(http|https)://(.*)", "");
        if (text.isEmpty()) {
            return "I was so excited by this #enews, I didn't have time to write anything. Check it out!";
        }
        return text;
    }

    private String generateTitle(ChannelHistory.Message message) {
        try {
            String pageLink = message.getPageLink();
            if (pageLink.startsWith("www")) {
                pageLink = "http:// " + pageLink;
            }
            return linkUnfurler.unfurl(pageLink).getTitle();
        } catch (IOException | UnsupportedUrlTypeException e) {
            return "#eNews link";
        }
    }

    private String generatePageLink(ChannelHistory.Message message) {
        String link = message.getPageLink();
        if (link.contains("youtube")) {
            return link;
        }
        if (link.contains("?")) {
            int startPositionOfParams = link.indexOf('?');
            link = link.substring(0, startPositionOfParams);
        }
        return link;
    }

    public static final class Factory {

        public ArticleEditor newInstance(boolean mock) {
            if (mock) {
                return newMockInstance();
            } else {
                return newInstance();
            }
        }

        public ArticleEditor newInstance() {
            return new ArticleEditor(new LinkUnfurler.Http(), new MissingImageFetcher(new Random()));
        }

        private ArticleEditor newMockInstance() {
            return new ArticleEditor(new LinkUnfurler.Mock(), new MissingImageFetcher(new Random()));
        }

    }
}
