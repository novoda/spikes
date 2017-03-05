function LoggingClient(client) {
    this.client = client;
}

LoggingClient.prototype.emit = function(event, dataToEmit) {
    if(this.client != undefined) {
        this.client.emit(event, dataToEmit);
    }
    console.log("\nEvent Emitted: " + event + "\nData Emitted: " + dataToEmit + "\n");
}

module.exports = LoggingClient;


