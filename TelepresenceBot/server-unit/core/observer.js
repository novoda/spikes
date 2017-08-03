function Observer() {
    return {
        notify: function(eventName, eventData) {
            return true;
        }
    };
}

module.exports = new Observer();
