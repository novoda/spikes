const expect = require('chai').expect
BotLocator = require('../core/botLocator.js');

const roomWithASingleBot = {
    'botId': {
        sockets: { 'botId': true },
        length: 1
    },
    'London': {
        sockets: { 'botId': true },
        length: 1
    }
};

const roomWhereBotIsConnectedToHuman = {
    'botId': {
        sockets: { 'botId': true, 'humanId': true },
        length: 2
    },
    'London': {
        sockets: { 'botId': true },
        length: 1
    }
};

const roomContainingMultipleBotsWhereOneIsConnectedToHuman = {
    'botId01': {
        sockets: { 'botId01': true, 'human01': true },
        length: 2
    },
    'botId02': {
        sockets: { 'botId02': true },
        length: 1
    },
    'London': {
        sockets: { 'botId01': true, 'botId02': true },
        length: 2
    }
};

describe('BotLocator Tests.', function () {

    it('Should give undefined when bot is not found in given room.', function (done) {
        const botLocator = new BotLocator(roomWithASingleBot);

        const bot = botLocator.locateFirstAvailableBotIn('Unexpected Room');

        expect(bot).to.be.undefined;
        done();
    });

    it('Should give bot id when bot room does not contain other sockets.', function (done) {
        const botLocator = new BotLocator(roomWithASingleBot);

        const bot = botLocator.locateFirstAvailableBotIn('London');

        expect(bot).to.equal('botId');
        done();
    });

    it('Should give undefined when bot room contains other sockets.', function (done) {
        const botLocator = new BotLocator(roomWhereBotIsConnectedToHuman);

        const bot = botLocator.locateFirstAvailableBotIn('London');

        expect(bot).to.be.undefined;
        done();
    });

    it('Should give first bot in room that contains multiple bots.', function (done) {
        const botLocator = new BotLocator(roomContainingMultipleBotsWhereOneIsConnectedToHuman);

        const bot = botLocator.locateFirstAvailableBotIn('London');

        expect(bot).to.equal('botId02');
        done();
    });

});
