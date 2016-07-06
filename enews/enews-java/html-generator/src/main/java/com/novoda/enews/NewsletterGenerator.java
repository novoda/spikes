package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.StringWriter;
import java.util.stream.Stream;

public class NewsletterGenerator {
    public String generate(Stream<ChannelHistory.Message> messageStream) {
        StringWriter writer = new StringWriter();
        new Html(writer) {
            {
                html();
                    body();
                        h1().text("#eNews from the week").end();
                    makeList();
                endAll();
                done();
            }
            Html makeList() {
                ul();
                messageStream.forEach(message -> {
                    li().text(message.toString()).end();
                });
                return end();
            }
        };
        return writer.toString();
    }
}
