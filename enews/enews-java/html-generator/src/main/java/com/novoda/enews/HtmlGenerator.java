package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.StringWriter;
import java.util.Collections;
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
                endAll();
                done();
            }
            Html makeList() {
                ul();
                for (ChannelHistory.Message message : messageStream.collect(Collectors.toList())) {
                    li()
                        .div()
                            .img().src(message.getImageUrl()).height("100").width("100").end()
                            .a().href(message.toUrl().toString()).end()
                            .p().text(message.getText()).end()
                        .end()
                    .end();
                }

                return end();
            }
        };
        return writer.toString();
    }
}
