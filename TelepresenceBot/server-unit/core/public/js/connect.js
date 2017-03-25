var intervalId;

function sendMessage(message) {
    var socket = io();
    socket.emit('chat message', message);
}

$(document).ready(function(){

    $("input").mousedown(function(){
        console.log("mouseDown");
        var message = $(this).val();
        intervalId = setInterval(function() {
            sendMessage(message);
        }, 500);
    });

    $("input").mouseup(function(){
        console.log("mouseUp");
        clearInterval(intervalId);
    });

});
