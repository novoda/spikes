package net.bonysoft.magicmirror.modules.twitter;

import android.content.Context;
import android.preference.PreferenceManager;

import com.novoda.notils.logger.simple.Log;

import net.bonysoft.magicmirror.BuildConfig;
import net.bonysoft.magicmirror.R;
import net.bonysoft.magicmirror.modules.DashboardModule;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterModule implements DashboardModule {

    private static final String DEFAULT_QUERY = "#droidconde";

    private final TwitterStream twitterStream;
    private final String query;
    private final TwitterListener listener;
    private boolean running = false;
    private TweetsFlowRegulator tweetsFlowRegulator;

    public static TwitterModule newInstance(Context context, TwitterListener listener) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        Configuration configuration = configurationBuilder
                .setOAuthConsumerKey(BuildConfig.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(BuildConfig.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(BuildConfig.TWITTER_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(BuildConfig.TWITTER_ACCESS_TOKEN_SECRET)
                .build();
        TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();

        String preferencesKey = context.getString(R.string.preference_key_tweet_filter);
        String query = PreferenceManager.getDefaultSharedPreferences(context).getString(preferencesKey, DEFAULT_QUERY);
        return new TwitterModule(twitterStream, query, listener);
    }

    TwitterModule(TwitterStream twitterStream, String query, TwitterListener listener) {
        this.twitterStream = twitterStream;
        this.query = query;
        this.listener = listener;

        initStream();
    }

    private void initStream() {
        tweetsFlowRegulator = TweetsFlowRegulator.newInstance(listener);
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                tweetsFlowRegulator.addTweet(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                // No-op
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                // No-op
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                // No-op
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                Log.w(warning);
            }

            @Override
            public void onException(Exception ex) {
                Log.e(ex);
            }
        });
    }

    @Override
    public void update() {
        if (running) {
            return;
        }
        running = true;

        tweetsFlowRegulator.startTweetPicker();
        twitterStream.filter(query);
    }

    @Override
    public void stop() {
        tweetsFlowRegulator.stopTweetPicker();
        twitterStream.cleanUp();
        running = false;
    }

    public interface TwitterListener {

        void onNextTweet(Status tweet);

    }
}
