var intervalId;

$('body').mousedown(function(event) {
    console.log("mouseDown");
    var message = $("input[type=button]").val();
    intervalId = setInterval(function(){
        sendMessage(message);
    }, 500);
});

function sendMessage(message) {
    var socket = io();
    socket.emit('chat message', message);
}

$('body').mouseup(function(event) {
    console.log("mouseUp");
    clearInterval(intervalId);
});
