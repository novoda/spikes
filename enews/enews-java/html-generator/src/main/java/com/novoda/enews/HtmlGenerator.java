package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.StringWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlGenerator {
    public String generate(Stream<Article> articleStream) {
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
                for (Article article : articleStream.collect(Collectors.toList())) {

                    li()
                        .div()
                            .img().src(article.getImage()).height("100").width("100").end()
                            .a().href(article.getPageLink()).end()
                            .p().text(article.getPageTitle()).end()
                            .p().text(article.getText()).end()
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
