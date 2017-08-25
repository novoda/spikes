package com.novoda.enews;

import com.googlecode.jatl.Html;
import com.larvalabs.linkunfurl.LinkInfo;
import com.larvalabs.linkunfurl.LinkUnfurl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlGenerator {
    public String generate(Stream<ChannelHistory.Message> messageStream) {
        StringWriter writer = new StringWriter();
        new Html(writer) {
            {
                html();
                    body();
                        h1().text("#eNews from the week").end();
                    makeList();
                    makeFooter();
                endAll();
                done();
            }

            void makeList() {
                ul();
                for (ChannelHistory.Message message : messageStream.collect(Collectors.toList())) {

                    String pageTitle;
                    try {
                        LinkInfo info = LinkUnfurl.unfurl(message.getPageLink(), 3000);
                        pageTitle = info.getTitle();
                    } catch (IOException e) {
                        pageTitle = "#eNews link";
                    }

                    li()
                        .div()
                            .img().src(message.getImageUrl()).height("100").width("100").end()
                            .a().href(message.toUrl().toString()).end()
                            .p().text(pageTitle).end()
                            .p().text(message.getText()).end()
                        .end()
                    .end();
                }

                end();
            }

            void makeFooter() {
                br();
                p()
                    .text("This newsletter was created with Java 8,")
                    .a()
                        .href("https://github.com/novoda/spikes/tree/master/enews/enews-java")
                        .text("and is available as open source.")
                    .end()
                .end();
            }
        };
        return writer.toString();
    }

    public static class Factory {

        public HtmlGenerator newInstance() {
            return new HtmlGenerator();
        }

    }
}
