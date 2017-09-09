package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.StringWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlGenerator {

    private final ShareBarHtmlGenerator shareBar = new ShareBarHtmlGenerator();

    public String generate(Stream<Article> articleStream) {
        StringWriter writer = new StringWriter();
        new Html(writer) {
            {
                html();
                meta().charset("utf-8").end();
                    head();
                        shareBar.injectCssInto(this);
                        style();
                            text("body {"
                                    + " padding: 10px;"
                                + "}"
                                + "#title h1 {"
                                    + " font: bold 40px/1.5 Helvetica, Verdana, sans-serif;"
                                    + " color: #26A3DB;"
                                    + " margin: 0px;"
                                + "}"
                                + "#title h2 {"
                                    + " font: bold 20px/1.3 Helvetica, Verdana, sans-serif;"
                                    + " color: #26A3DB;"
                                    + " margin: 0px;"
                                + "}"
                                + "#title a {"
                                    + " text-decoration: none;"
                                    + " color: #26A3DB;"
                                + "}"
                                + "li {"
                                    + " list-style-type: none; margin: 0;"
                                    + " padding: 10px;"
                                    + " overflow: auto;"
                                + "}"
                                + "li a {"
                                    + " text-decoration: none;"
                                    + " color: black;"
                                + "}"
                                + "#article-title {"
                                    + " font: bold 18px/1.5 Helvetica, Verdana, sans-serif;"
                                    + " color: #2d2d2d;"
                                    + " width: auto;"
                                    + " max-width: 400px;"
                                + "}"
                                + "#article-text {"
                                    + " font: 200 14px/1.5 Open Sans, serif;"
                                    + " width: auto;"
                                    + " max-width: 400px;"
                                + "}"
                                // Small / Default
                                + "#article-image { "
                                    + " width: 150px; height: 84px;"
                                    + " object-fit: cover;"
                                    + " border-radius: 6px; background-color: #fafafa; padding: 10px;"
                                + "}"
                                // Mobile Medium
                                + " @media only screen and (min-width: 375px) {"
                                    + "#article-image { "
                                    + " width: 300px; height: 169px;"
                                    + "}"
                                + "}"
                                // Mobile Large
                                + " @media only screen and (min-width: 425px) {"
                                    + "#article-image { "
                                    + " width: 400px; height: 225px;"
                                    + "}"
                                + "}"
                                // Tablet
                                + " @media only screen and (min-width: 768px) {"
                                    + "#article-image { "
                                    + " width: 450px; height: 253px;"
                                    + "}"
                                + "}"
                                // Laptop
                                + " @media only screen and (min-width: 1024px) {"
                                    + "#article-image { "
                                    + " width: 500px; height: 281px;"
                                    + "}"
                                + "}"
                                + ""
                            );
                        end();
                    end();
                     body()
                        .div()
                            .id("title")
                            .h1()
                                .text("#enews of the week")
                            .end()
                            .h2()
                                .text("powered by ")
                                .a().href("https://twitter.com/novoda").target("_blank").text("@novoda").end()
                            .end()
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
                                    .img()
                                        .id("article-image")
                                        .src(article.getImage())
                                    .end()
                                .end()
                                .p()
                                    .id("article-title")
                                    .text(article.getPageTitle())
                                .end()
                                .p()
                                    .id("article-text")
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
                makeSourceFooter();
//                shareBar.injectHtmlInto(this);
            }

            private void makeSourceFooter() {
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
