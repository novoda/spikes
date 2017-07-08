var intervalId;
var socket = io();

$(document).ready(function(){

    $("input").mousedown(function(){
        console.log("mouseDown");
        var message = $(this).val();
        intervalId = setInterval(function() {
            sendMessage(message);
        }, 100);
    });

    function sendMessage(message) {
        socket.emit('chat message', message);
        $('#messages').append($('<li>').text(message));
    }

    $("input").mouseup(function(){
        console.log("mouseUp");
        clearInterval(intervalId);
    });

});
