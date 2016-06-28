package net.bonysoft.magicmirror.modules.twitter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.Status;

class TweetsBuffer {

    private static final int MAX_TWEETS_BUFFER_LENGTH = 10;

    private final Queue<Status> buffer;

    TweetsBuffer() {
        this.buffer = new ConcurrentLinkedQueue<>();
    }

    void addTweet(Status tweet) {
        while (buffer.size() > MAX_TWEETS_BUFFER_LENGTH) {
            buffer.poll();
        }
        buffer.add(tweet);
    }

    Status pollTweet() {
        return buffer.poll();
    }
}
