package com.novoda.tpbot.bot;

class BotPresenter {

    private final BotTpService tpService;
    private final BotView botView;

    BotPresenter(BotTpService tpService, BotView botView) {
        this.tpService = tpService;
        this.botView = botView;
    }

    void startPresenting() {
        tpService.connect();
    }

}
