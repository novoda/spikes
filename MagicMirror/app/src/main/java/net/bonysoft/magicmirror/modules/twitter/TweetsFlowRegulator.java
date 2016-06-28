package net.bonysoft.magicmirror.modules.twitter;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import twitter4j.Status;

import static net.bonysoft.magicmirror.modules.twitter.TwitterModule.*;

class TweetsFlowRegulator {

    private static final long REFRESH_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(4);

    private final Handler handler;
    private final TweetsBuffer tweetsBuffer;
    private final TweetPicker tweetPickerAction;

    static TweetsFlowRegulator newInstance(TwitterListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        TweetsBuffer tweetsBuffer = new TweetsBuffer();
        return new TweetsFlowRegulator(handler, tweetsBuffer, listener);
    }

    TweetsFlowRegulator(Handler handler, TweetsBuffer tweetsBuffer, TwitterListener listener) {
        this.handler = handler;
        this.tweetsBuffer = tweetsBuffer;
        this.tweetPickerAction = new TweetPicker(tweetsBuffer, listener);
    }

    private void scheduleNextBeat() {
        handler.postDelayed(refreshAction, REFRESH_DELAY_MILLIS);
    }

    void startTweetPicker() {
        handler.post(refreshAction);
    }

    void stopTweetPicker() {
        handler.removeCallbacks(refreshAction);
    }

    void addTweet(Status tweet) {
        tweetsBuffer.addTweet(tweet);
    }

    private final Runnable refreshAction = new Runnable() {
        @Override
        public void run() {
            handler.post(tweetPickerAction);
            scheduleNextBeat();
        }
    };

    private static class TweetPicker implements Runnable {

        private final TweetsBuffer tweetsBuffer;
        private final TwitterListener listener;

        TweetPicker(TweetsBuffer tweetsBuffer, TwitterListener listener) {
            this.tweetsBuffer = tweetsBuffer;
            this.listener = listener;
        }

        @Override
        public void run() {
            Status tweet = tweetsBuffer.pollTweet();
            if (tweet != null) {
                listener.onNextTweet(tweet);
            }
        }
    }

}
