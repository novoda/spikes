package com.novoda.enews;

class NewsletterGenerator {
    private final MailChimpWebService webService;

    public NewsletterGenerator(MailChimpWebService webService) {
        this.webService = webService;
    }

    public void generate(String html) {
        webService.postCampaign(new Campaign());
    }
}
