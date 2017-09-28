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
                            text("#newsletter-body {"
                                    + " display: flex;"
                                    + " flex-direction: column;"
                                    + " align-items: center;"
                                + "}"
                                + "#header-image { "
                                    + " background: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_PhoneSmall.png);"
                                    + " width:360px; height:190px"
                                + "}"
                                + "#title a {"
                                    + " text-decoration: none;"
                                    + " color: #26A3DB;"
                                + "}"
                                + "#title p {"
                                    + " font: 300 1.2em/1.8 Open Sans, regular;"
                                    + " color: #6d6d6d;"
                                    + " text-align: left;"
                                + "}"
                                + "ul {"
                                    + " -webkit-padding-start: 0px;"
                                + "}"
                                + "li {"
                                    + " list-style-type: none;"
                                + "}"
                                + "li a {"
                                    + " text-decoration: none;"
                                    + " color: black;"
                                + "}"
                                + "#content { "
                                + "}"
                                + "#article-image { "
                                    + " width: 150px; height: 84px;"
                                    + " object-fit: cover;"
                                + "}"
                                + "#article-title {"
                                    + " font: bold 20px/1.5 Open Sans, Helvetica, Verdana, sans-serif;"
                                    + " color: #000000;"
                                    + " max-width: 400px;"
                                + "}"
                                + "#article-text {"
                                    + " font: 200 16px/1.5 Open Sans, regular;"
                                    + " color: #000000;"
                                    + " max-width: 400px;"
                                + "}"
                                // Mobile Small
                                + " @media only screen and (min-width: 320px) {"
                                    + "#header-image { "
                                        + " background: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_PhoneSmall.png);"
                                        + " width:360px; height:190px"
                                    + "}"
                                    + "#intro {"
                                        + "max-width: 360px;"
                                    +"}"
                                    + "#article-image { "
                                        + " width: 360px; height: 202px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font: bold 18px/1.5 Open Sans, Helvetica, Verdana, sans-serif;"
                                        + " color: #000000;"
                                        + " max-width: 360px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font: 200 14px/1.5 Open Sans, regular;"
                                        + " color: #000000;"
                                        + " max-width: 360px;"
                                    + "}"
                                + "}"
                                // Mobile Large
                                + " @media only screen and (min-width: 425px) {"
                                    + "#header-image { "
                                        + " background: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_PhoneLarge.png);"
                                        + " width:425px; height:188px"
                                    + "}"
                                    + "#intro {"
                                        + "max-width: 360px;"
                                    +"}"
                                    + "#article-image { "
                                        + " width: 360px; height: 202px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font: bold 18px/1.5 Open Sans, Helvetica, Verdana, sans-serif;"
                                        + " color: #000000;"
                                        + " max-width: 360px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font: 200 14px/1.5 Open Sans, regular;"
                                        + " color: #000000;"
                                        + " max-width: 360px;"
                                    + "}"
                                + "}"
                                // Tablet
                                + " @media only screen and (min-width: 768px) {"
                                    + "#header-image { "
                                        + " background: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_Tablet.png);"
                                        + " width:600px; height:180px"
                                    + "}"
                                    + "#intro {"
                                    +   "max-width: 600px;"
                                    +"}"
                                    + "#article-image { "
                                        + " width: 480px; height: 268px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font: bold 20px/1.5 Open Sans, Helvetica, Verdana, sans-serif;"
                                        + " color: #000000;"
                                        + " max-width: 480px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font: 200 16px/1.5 Open Sans, regular;"
                                        + " color: #000000;"
                                        + " max-width: 480px;"
                                    + "}"
                                + "}"
                                // Laptop
                                + " @media only screen and (min-width: 1024px) {"
                                    + "#header-image { "
                                        + " background: url(https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/Header_desktop.png);"
                                        + " width:800px; height:253px"
                                    + "}"
                                    + "#intro {"
                                        + "max-width: 800px;"
                                    +"}"
                                    + "#article-image { "
                                        + " width: 480px; height: 268px;"
                                    + "}"
                                    + "#article-title {"
                                        + " font: bold 20px/1.5 Open Sans, Helvetica, Verdana, sans-serif;"
                                        + " color: #000000;"
                                        + " max-width: 480px;"
                                    + "}"
                                    + "#article-text {"
                                        + " font: 200 16px/1.5 Open Sans, regular;"
                                        + " color: #000000;"
                                        + " max-width: 480px;"
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
                                    .img()
                                        .id("header-image")
                                        .src("https://s3-eu-west-1.amazonaws.com/novoda-public-image-bucket/blank.gif")
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
