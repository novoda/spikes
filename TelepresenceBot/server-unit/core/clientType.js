ClientType = {
    BOT : "bot",
    HUMAN : "human",
    TEST : "test",
    from : function(rawClientType) {
        for(var key in this) {
            if(ClientType[key] == rawClientType) {
                return ClientType[key];
            }
        }
        return undefined;
    }
}

module.exports = ClientType;
