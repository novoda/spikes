function sendMessage(message) {
    var socket = io();
    socket.emit('chat message', message);
}
