package com.novoda.enews;

import com.googlecode.jatl.Html;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlGenerator {

    private final String head;
    private final String css;
    private final String preBody;
    private final String articleTemplate;
    private final String postBody;

    HtmlGenerator(String head, String css, String preBody, String articleTemplate, String postBody) {
        this.head = head;
        this.css = css;
        this.preBody = preBody;
        this.articleTemplate = articleTemplate;
        this.postBody = postBody;
    }

    public String generate(Stream<Article> articleStream) {
        StringWriter writer = new StringWriter();
        new Html(writer) {
            {
                html();
                    meta().charset("utf-8").end();
                    head()
                        .raw(head)
                        .style()
                            .raw(css)
                        .end()
                    .end()
                    .body()
                        .raw(preBody);
                        makeList();
                        raw(postBody)
                    .end()
                .endAll()
                .done();
            }

            void makeList() {
                for (Article article : articleStream.collect(Collectors.toList())) {
                    String localArticle = articleTemplate
                            .replace("${articleUrl}", article.getPageLink())
                            .replace("${articleImageUrl}", article.getImage())
                            .replace("${articleTitle}", article.getPageTitle())
                            .replace("${articleText}", article.getText());
                    raw(localArticle);
                }
            }

        };
        return writer.toString();
    }

    public static class Factory {

        public HtmlGenerator newInstance() {
            return new HtmlGenerator(
                    getAsString("./mailchimp-template-head.html"),
                    getAsString("./mailchimp-template-style.css"),
                    getAsString("./mailchimp-template-pre-articles.html"),
                    getAsString("./mailchimp-template-article.html"),
                    getAsString("./mailchimp-template-post-articles.html"));
        }

        private String getAsString(String name) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream stream = classLoader.getResourceAsStream(name);
            try (java.util.Scanner scanner = new java.util.Scanner(stream)) {
                return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }
        }

    }

}
