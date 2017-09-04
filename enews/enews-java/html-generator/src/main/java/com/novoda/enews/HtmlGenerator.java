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
                        style(" padding: 10px;");
                        h1()
                            .style("font: bold 40px/1.5 Helvetica, Verdana, sans-serif; color: #34c5db;")
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

                    li().style("list-style-type: none; margin: 0; padding: 10px; overflow: auto;")
                        .a().style("display: block; text-decoration: none; color: black;").href(article.getPageLink())
                            .div()
                                .img()
                                    .style("float: left; margin: 0 15px 0 0;")
                                    .src(article.getImage()).height("100")
                                    .width("100")
                                .end()
                                .p()
                                    .style("font: bold 20px/1.5 Helvetica, Verdana, sans-serif;")
                                    .text(article.getPageTitle())
                                .end()
                                .p()
                                    .style("font: 200 12px/1.5 Georgia, Times New Roman, serif;")
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
