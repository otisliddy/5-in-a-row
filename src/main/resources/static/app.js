var stompClient = null;
var playerId;
var playerName;

function connect() {
    var socket = new SockJS('/5-in-a-row');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $("#btn-start").prop("disabled", false);
        stompClient.subscribe('/user/queue/start', function (response) {
            getReadyForGameStart(response);
            stompClient.subscribe('/topic/moves', function (response) {
                handleGameState(JSON.parse(response.body));
            });
            stompClient.subscribe('/topic/disconnected', function (response) {
                $('#lbl-cmd-prompt').text(response.body);
                setGameOver();
            });
            stompClient.subscribe('/user/queue/illegal-move', function (response) {
                var illegalMove = JSON.parse(response.body);
                if (illegalMove.playerId == playerId) {
                    $('#lbl-cmd-prompt').text(illegalMove.message);
                }
            });
        });
    }, function (error) {
        console.log("STOMP error: " + error);
    });
}

function getReadyForGameStart(response) {
    var player = JSON.parse(response.body);
    playerId = player.id;
    playerName = player.name;
    if (playerId == 1) {
        $('#lbl-cmd-prompt').text("Waiting for the other player to join...");
    }
}

function setGameOver() {
    disableMoving(true);
    stompClient.disconnect(function () {
    });
}

function handleGameState(gameState) {
    $('#div-game-grid').html(parseGridHtml(gameState.grid));

    if (gameState.status == 'IN_PROGRESS') {
        if (isThisPlayer(gameState.playerToMove)) {
            $('#lbl-cmd-prompt').text("It's your turn " + playerName + ", please enter column (1-" + gameState.grid[0].length + "):");
        } else {
            $('#lbl-cmd-prompt').text("Waiting for " + gameState.playerToMove.name + " to move...");
        }
        disableMoving(!isThisPlayer(gameState.playerToMove));
    }
    else {
        setGameOver();
        if (gameState.status == 'OVER_WON') {
            if (isThisPlayer(gameState.playerToMove)) {
                $('#lbl-cmd-prompt').text("Congratulations " + playerName + ", you've won!");
            }
            else {
                $('#lbl-cmd-prompt').text("You've lost. " + gameState.playerToMove.name + " has won.");
            }
        }
        else if (gameState.status == 'OVER_DRAWN') {
            $('#lbl-cmd-prompt').text("The game has ended in a draw.");
        }
    }
}

function parseGridHtml(gridArray) {
    var html = '<tt>';
    for (var row = gridArray.length - 1; row >= 0; row--) {
        for (var column = 0; column < gridArray[row].length; column++) {
            var symbol = '&nbsp;';
            if (gridArray[row][column] == 1) symbol = 'X';
            else if (gridArray[row][column] == 2) symbol = 'O';
            html += '[' + symbol + ']&nbsp; ';
        }
        html += '<br/>'
    }
    return html
}

function isThisPlayer(player) {
    return player.id == playerId;
}

function disableMoving(disabled) {
    $("#btn-player-move").prop("disabled", disabled);
}

function sendName() {
    if ($("#txt-name").val() != '') {
        stompClient.send("/fiveinarow/start", {}, $("#txt-name").val());
        $("#div-enter-name").hide();
        $("#div-game").show();
    }
}

function move() {
    var columnValue = $("#txt-player-move").val();
    if (!isNaN(parseFloat(columnValue)) && !isNaN(columnValue - 0)) {
        stompClient.send("/fiveinarow/move", {}, $("#txt-player-move").val());
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#btn-start").click(function () { sendName(); });
    $("#btn-player-move").click(function () { move(); });
    connect();
});