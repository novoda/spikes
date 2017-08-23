module.exports = ClientType = {
    BOT: 'bot',
    HUMAN: 'human',
    TEST: 'test',
    from: function (rawClientType) {
        for (let key in this) {
            if (ClientType[key] == rawClientType) {
                return ClientType[key];
            }
        }
        return undefined;
    }
}
