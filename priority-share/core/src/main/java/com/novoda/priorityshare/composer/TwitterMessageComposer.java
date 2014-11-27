package com.novoda.priorityshare.composer;

/**
 * Twitter-specific {@link com.novoda.priorityshare.composer.MaxLengthMessageComposer}
 * implementation that imposes Twitter's max tweet length of 140
 * characters and considers <i>t.co</i>'s 22-char shortened URLs length.
 */
public class TwitterMessageComposer extends MaxLengthMessageComposer {

    private static final int MAX_TWITTER_MESSAGE_LENGTH = 140;
    private static final String TWITTER_SHORT_URL_FORMAT = "http://t.co/XXXXXXXXXX";
    private static final int TWITTER_SHORTENED_URL_LENGTH = TWITTER_SHORT_URL_FORMAT.length();

    public TwitterMessageComposer() {
        super(MAX_TWITTER_MESSAGE_LENGTH);
    }

    @Override
    protected int getShortenedUrlLengthFor(String uri) {
        return TWITTER_SHORTENED_URL_LENGTH;
    }
}
