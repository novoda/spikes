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
                meta().charset("utf-8").end();
                    head();
                        style();
                            text("body { padding: 10px;}"
                                + "h1 {font: bold 40px/1.5 Helvetica, Verdana, sans-serif; color: #26A3DB;}"
                                + "li {list-style-type: none; margin: 0; padding: 10px; overflow: auto;}"
                                + "a {text-decoration: none; color: black;}"
                                + ""
                                + ""
                            );
                        end();
                    end();
                     body();
                        h1()
                            .text("#eNews from the week")
                        .end()
                        .div();
                            makeList();
                        end();
                        div();
                            makeFooter();
                        end();
                endAll();
                done();
            }

            void makeList() {
                ul();
                for (Article article : articleStream.collect(Collectors.toList())) {

                    li()
                        .a().href(article.getPageLink())
                            .div()
                                .div()
                                    .style("max-width: 400px; max-height: 220px;")
                                    .img()
                                        .style("margin: 0 15px 0 0;" +
                                                " max-width: 100%; max-height: 220px; width: auto; height: auto;" +
                                                " object-fit: cover;" +
                                                " border-radius: 6px; background-color: #fafafa; padding: 10px;")
                                        .src(article.getImage())
                                    .end()
                                .end()
                                .p()
                                    .style("font: bold 18px/1.5 Helvetica, Verdana, sans-serif; color: #2d2d2d;" +
                                            " width: auto; max-width: 400px;")
                                    .text(article.getPageTitle())
                                .end()
                                .p()
                                    .style("font: 200 14px/1.5 Open Sans, serif;" +
                                            " width: auto; max-width: 400px;")
                                    .text(article.getText())
                                .end()
                            .end()
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
