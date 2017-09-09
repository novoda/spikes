package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class ShareBarHtmlGenerator {

    private Html html;

    /**
     * Expected to be called inside <head> here </head>
     */
    void injectCssInto(Html html) {
        this.html = html;
        addFontAwesomeCss();
        addShareFooterCss();
    }

    private void addFontAwesomeCss() {
        html.script().src("https://use.fontawesome.com/2c3bbf0ac3.js").end();
    }

    private void addShareFooterCss() {
        html.style().text("" +
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
        html.end();
    }

    void injectHtmlInto(Html html) {
        this.html = html;
        makeShareFooter();
    }

    private void makeShareFooter() {
        html.p().text("Share this newsletter:").end();

        String title = encode("Checkout this #eNews letter Archive #234");
        String text = encode("Checkout this #eNews letter Archive #234");
        String url = "https://TODO-Archive-Url.com";
        String via = "Novoda";

        html.bind("shareTitle", title);
        html.bind("shareText", text);
        html.bind("shareUrl", url);
        html.bind("sharedVia", via);

        makeTwitterFooter();
        makeGooglePlusFooter();
        makeFacebookFooter();
        makeStumbleUponFooter();
        makeRedditFooter();
        makeLinkedInFooter();
        makeEmailFooter();
    }

    private void makeTwitterFooter() {
        html.a()
            .classAttr("share-btn twitter")
            .target("_blank")
            .href("http://twitter.com/share?text=${shareText}&url=${shareUrl}&via=${sharedVia}")
            .i().classAttr("fa fa-twitter").text("").end()
        .end();
    }

    private void makeGooglePlusFooter() {
        html.a()
            .classAttr("share-btn google-plus")
            .target("_blank")
            .href("https://plus.google.com/share?url=${shareUrl}")
            .i().classAttr("fa fa-google-plus").text("").end()
        .end();
    }

    private void makeFacebookFooter() {
        html.a()
            .classAttr("share-btn facebook")
            .target("_blank")
            .href("http://www.facebook.com/sharer/sharer.php?u=${shareUrl}")
            .i().classAttr("fa fa-facebook").text("").end()
        .end();
    }

    private void makeRedditFooter() {
        html.a()
            .classAttr("share-btn reddit")
            .target("_blank")
            .href("http://reddit.com/submit?url=${shareUrl}&title=${shareTitle}")
            .i().classAttr("fa fa-reddit").text("").end()
        .end();
    }

    private void makeStumbleUponFooter() {
        html.a()
            .classAttr("share-btn stumbleupon")
            .target("_blank")
            .href("http://www.stumbleupon.com/submit?url=${shareUrl}&title=${shareTitle}")
            .i().classAttr("fa fa-stumbleupon").text("").end()
        .end();
    }

    private void makeLinkedInFooter() {
        html.a()
            .classAttr("share-btn linkedin")
            .target("_blank")
            .href("http://www.linkedin.com/shareArticle?url=${shareUrl}&title=<${shareTitle}>&summary=${shareText}&source=${shareUrl}")
            .i().classAttr("fa fa-linkedin").text("").end()
        .end();
    }

    private void makeEmailFooter() {
        html.a()
            .classAttr("share-btn email")
            .target("_blank")
            .href("mailto:?subject=${shareTitle}&body=${shareText}%0A%0A${shareUrl}")
            .i().classAttr("fa fa-envelope").text("").end()
        .end();
    }

    private static String encode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("can't encode, so whats the point.", e);
        }
    }
}
