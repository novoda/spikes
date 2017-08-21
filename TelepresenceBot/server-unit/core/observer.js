module.exports = function Observer() {
    return {
        notify: function(eventName, eventData) {
            return true;
        }
    };
}
