package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
                        script().src("https://use.fontawesome.com/2c3bbf0ac3.js").end();
                        style();
                            text("body { padding: 10px;}"
                                + "h1 {font: bold 40px/1.5 Helvetica, Verdana, sans-serif; color: #26A3DB;}"
                                + "li {list-style-type: none; margin: 0; padding: 10px; overflow: auto;}"
                                + "li a {text-decoration: none; color: black;}"
                                + ""
                            );
                            addShareFooterCss();
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

            private void addShareFooterCss() {
                text("" +
                        ".share-btn {" +
                        "    display: inline-block;" +
                        "    color: #ffffff;" +
                        "    border: none;" +
                        "    padding: 0.5em;" +
                        "    width: 4em;" +
                        "    box-shadow: 0 2px 0 0 rgba(0,0,0,0.2);" +
                        "    outline: none;" +
                        "    text-align: center;" +
                        "}" +
                        "" +
                        ".share-btn:hover {" +
                        "  color: #eeeeee;" +
                        "}" +
                        "" +
                        ".share-btn:active {" +
                        "  position: relative;" +
                        "  top: 2px;" +
                        "  box-shadow: none;" +
                        "  color: #e2e2e2;" +
                        "  outline: none;" +
                        "}" +
                        "" +
                        ".share-btn.twitter     { background: #55acee; }" +
                        ".share-btn.google-plus { background: #dd4b39; }" +
                        ".share-btn.facebook    { background: #3B5998; }" +
                        ".share-btn.stumbleupon { background: #EB4823; }" +
                        ".share-btn.reddit      { background: #ff5700; }" +
                        ".share-btn.linkedin    { background: #4875B4; }" +
                        ".share-btn.email       { background: #444444; }");
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
                makeSourceFooter();
                makeShareFooter();
            }

            private void makeShareFooter() {
                p().text("Share this newsletter:").end();

                String title = encode("Checkout this #eNews letter Archive #234");
                String text = encode("Checkout this #eNews letter Archive #234");
                String url = "https://TODO-Archive-Url.com";
                String via = "Novoda";

                bind("shareTitle", title);
                bind("shareText", text);
                bind("shareUrl", url);
                bind("sharedVia", via);

                makeTwitterFooter();
                makeGooglePlusFooter();
                makeFacebookFooter();
                makeStumbleUponFooter();
                makeRedditFooter();
                makeLinkedInFooter();
                makeEmailFooter();
            }

            private void makeTwitterFooter() {
                a()
                    .classAttr("share-btn twitter")
                    .target("_blank")
                    .href("http://twitter.com/share?text=${shareText}&url=${shareUrl}&via=${sharedVia}")
                    .i().classAttr("fa fa-twitter").text("").end()
                .end();
            }

            private void makeGooglePlusFooter() {
                a()
                    .classAttr("share-btn google-plus")
                    .target("_blank")
                    .href("https://plus.google.com/share?url=${shareUrl}")
                    .i().classAttr("fa fa-google-plus").text("").end()
                .end();
            }

            private void makeFacebookFooter() {
                a()
                    .classAttr("share-btn facebook")
                    .target("_blank")
                    .href("http://www.facebook.com/sharer/sharer.php?u=${shareUrl}")
                    .i().classAttr("fa fa-facebook").text("").end()
                .end();
            }

            private void makeRedditFooter() {
                a()
                    .classAttr("share-btn reddit")
                    .target("_blank")
                    .href("http://reddit.com/submit?url=${shareUrl}&title=${shareTitle}")
                    .i().classAttr("fa fa-reddit").text("").end()
                .end();
            }

            private void makeStumbleUponFooter() {
                a()
                    .classAttr("share-btn stumbleupon")
                    .target("_blank")
                    .href("http://www.stumbleupon.com/submit?url=${shareUrl}&title=${shareTitle}")
                    .i().classAttr("fa fa-stumbleupon").text("").end()
                .end();
            }

            private void makeLinkedInFooter() {
                a()
                    .classAttr("share-btn linkedin")
                    .target("_blank")
                    .href("http://www.linkedin.com/shareArticle?url=${shareUrl}&title=<${shareTitle}>&summary=${shareText}&source=${shareUrl}")
                    .i().classAttr("fa fa-linkedin").text("").end()
                .end();
            }

            private void makeEmailFooter() {
                a()
                    .classAttr("share-btn email")
                    .target("_blank")
                    .href("mailto:?subject=${shareTitle}&body=${shareText}%0A%0A${shareUrl}")
                    .i().classAttr("fa fa-envelope").text("").end()
                .end();
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

    private static String encode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("can't encode, so whats the point.", e);
        }
    }
}
