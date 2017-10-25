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
                            text("" +
                                "* {"
                                    + " margin: 0;"
                                    + " padding: 0;"
                                + "}"
                                + "#newsletter-body {"
                                    + " padding: 5px;"
                                    + "}"
                                + "#title a {"
                                    + " text-decoration: none;"
                                    + " color: #26A3DB;"
                                + "}"
                                + "#intro {"
                                    + " font: 300 1.2em/1.8 Open Sans, Helvetica, Verdana, sans-serif, regular;"
                                    + " color: #6d6d6d;"
                                    + " text-align: justify;"
                                + "}"
                                + "li {"
                                    + " list-style-type: none;"
                                    + " padding-top: 20px;"
                                    + " padding-bottom: 20px;"
                                + "}"
                                + "li a {"
                                    + " text-decoration: none;"
                                    + " color: black;"
                                + "}"
                                + "#article {"
                                    + "width: 70%;"
                                    + "margin: 0 auto;"
                                + "}"
                                + "#article-image { "
                                    + " object-fit: cover;"
                                + "}"
                                + "#article-title {"
                                    + " color: #000000;"
                                    + " font-weight: bold;"
                                    + " font-family: Open Sans, Helvetica, Verdana, sans-serif;"
                                    + " line-height: 1.5;"
                                + "}"
                                + "#article-text {"
                                    + " color: #000000;"
                                    + " font-weight: regular;"
                                    + " font-family: Open Sans, Helvetica, Verdana, sans-serif;"
                                    + " line-height: 1.5;"
                                + "}"
                                // Mobile Small
                                + " @media only screen and (min-width: 320px) {"
                                    + "#newsletter-body {"
                                        + "width:360px;"
                                    + "}"
                                    + "#header-image { "
                                        + " background-image: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_PhoneSmall.png);"
                                        + " width: 360px; height: 190px;"
                                    + "}"
                                    + "#article-image { "
                                        + " width: 360px; height: 202px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font-size: 18px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font-size: 14px;"
                                    + "}"
                                + "}"
                                // Mobile Large
                                + " @media only screen and (min-width: 425px) {"
                                    + "#newsletter-body {"
                                        + "width:425px;"
                                    + "}"
                                    + "#header-image { "
                                        + " background-image: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_PhoneLarge.png);"
                                        + " width: 360px; height: 190px;"
                                    + "}"
                                    + "#article-image { "
                                        + " width: 360px; height: 202px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font-size: 18px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font-size: 14px;"
                                    + "}"
                                + "}"
                                // Tablet
                                + " @media only screen and (min-width: 768px) {"
                                    + "#newsletter-body {"
                                        + "width:600px;"
                                    + "}"
                                    + "#header-image { "
                                        + " background-image: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_Tablet.png);"
                                        + " width: 600px; height: 180px;"
                                    + "}"
                                    + "#article-image { "
                                        + " width: 480px; height: 268px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font-size: 20px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font-size: 16px;"
                                    + "}"
                                + "}"
                                // Laptop
                                + " @media only screen and (min-width: 1024px) {"
                                    + "#newsletter-body {"
                                        + "width:800px;"
                                    + "}"
                                    + "#header-image { "
                                        + " background-image: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_desktop.png);"
                                        + " width: 800px; height: 253px;"
                                    + "}"
                                    + "#article-image { "
                                        + " width: 480px; height: 268px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font-size: 20px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font-size: 16px;"
                                    + "}"
                                + "}"
                                + ""
                            );
                        end();
                    end();
                     body()
                        .div()
                            .id("newsletter-body")
                            .div()
                                .id("title")
                                 .a().href("https://twitter.com/novoda").target("_blank")
                                    .div()
                                        .id("header-image")
                                    .end()
                                .end()
                                .p()
                                    .id("intro")
                                    .text("Welcome to the latest edition."
                                            + " For those of you unacquainted with how this newsletter works."
                                            + " See below for a brief explanation."
                                            + " Everyone else, welcome back and skip to the first article!")
                                    .br()
                                    .br()
                                    .text("Each day the whole of Novoda come together to share project updates, insights and exciting opportunities happening across the company."
                                            + " One of these snippets is #enews where we share interesting things we've read or viewed on the web."
                                            + " Our serverless technology peon then takes this knowledge and curates it as a summary each week."
                                            + " Therefore the text below is exactly what was written by the author as they shared the news on slack, so apologies for fast finger spelling mistakes or slightly missing context."
                                            + " :-)")
                                .end()
                            .end()
                            .div()
                                .id("content");
                                makeList();
                            end();
                            div();
                                makeFooter();
                            end();
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
                                .id("article")
                                .img()
                                    .id("article-image")
                                    .src(article.getImage())
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
