package com.novoda.enews;

public class Article {

    private final String image;
    private final String pageTitle;
    private final String text;
    private final String pageLink;

    public Article(String image, String pageTitle, String text, String pageLink) {
        this.image = image;
        this.pageTitle = pageTitle;
        this.text = text;
        this.pageLink = pageLink;
    }

    public String getImage() {
        return image;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getText() {
        return text;
    }

    public String getPageLink() {
        return pageLink;
    }

    @Override
    public String toString() {
        return "Article{" +
                "\nimage='" + image + '\'' +
                ",\n pageTitle='" + pageTitle + '\'' +
                ",\n text='" + text + '\'' +
                ",\n pageLink='" + pageLink + '\'' +
                "\n}";
    }
}
