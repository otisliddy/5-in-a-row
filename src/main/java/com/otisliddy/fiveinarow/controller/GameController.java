package com.otisliddy.fiveinarow.controller;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.otisliddy.fiveinarow.config.SystemProperties;
import com.otisliddy.fiveinarow.exception.IllegalMoveException;
import com.otisliddy.fiveinarow.game.GameState;
import com.otisliddy.fiveinarow.game.GameStatus;
import com.otisliddy.fiveinarow.game.Player;

/**
 * Receives and processes WebSocket messages prefixed by '/fiveinarow'
 */
@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private static final int NUM_ROWS = SystemProperties.NUM_ROWS.getValue();
    private static final int NUM_COLUMNS = SystemProperties.NUM_COLUMNS.getValue();
    private static final int IN_A_ROW_TO_WIN = SystemProperties.IN_A_ROW_TO_WIN.getValue();
    private static final String ENDPOINT_START = "/start";
    private static final String ENDPOINT_MOVE = "/move";
    private static final String QUEUE_START = "/queue/start";
    private static final String TOPIC_MOVES = "/topic/moves";
    private static final String QUEUE_ILLEGAL_MOVE = "/queue/illegal-move";
    private static final String TOPIC_DISCONNECTED = "/topic/disconnected";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GameState gameState;
    private Player playerOne = null;
    private Player playerTwo = null;
    private AtomicInteger playerId = new AtomicInteger(1);

    /**
     * Processes a message request from a client to start a game as a new player. This endpoitn must be called before a client calls
     * {@value ENDPOINT_MOVE}. The method sends a {@link Player} object to the client which represents the client's new player details.
     * <p>
     * If this method is called for the second time for a game, a message is also sent to {@value TOPIC_MOVES} for connected clients to receive
     * the initial game state.
     * </p>
     *
     * <p>Only two active clients during a game are supported, i.e. two players. If this method is called for a third time during the same game, a
     * player will be returned with ID=3, but that player should not be allowed by the client to call {@value ENDPOINT_MOVE}.
     * </p>
     *
     * @param name
     *            the name to assign to the player to be associated with the calling client.
     * @return the {@code Player} object to be  associated with the calling client.
     */
    @MessageMapping(ENDPOINT_START)
    @SendToUser(QUEUE_START)
    public Player start(@Payload String name) {
        logger.info("/start endpoint called with payload '{}' ", name);

        final Player player = new Player(name, playerId.getAndIncrement());
        logger.debug("Creating player {}", player);
        if (player.getId() == 1) {
            playerOne = player;
            gameState.setPlayerToMove(player);
        } else if (player.getId() == 2) {
            playerTwo = player;
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            // delay to make sure /topic/moves message is sent after /queue/start
            executor.schedule(() -> messagingTemplate.convertAndSend(TOPIC_MOVES, gameState), 50, TimeUnit.MILLISECONDS);
        }

        return player;
    }

    /**
     * Processes a message request from a client to make a move in the provided grid column. Upon successfgul updating of the game, the {@code
     * GameState} is sent to {@value TOPIC_MOVES}. The game state may have three statuses:
     * <ol>
     * <li>{@code IN_PROGRESS}: The game has been started but has not been completed.</li>
     * <li>{@code IN_PROGRESS}: The game has been won by a player In this case the game state's {@code playerToMove} indicates the winner of
     * the game.
     * </li>
     * <li>{@code IN_PROGRESS}: The game has ended in a draw.</li>
     * </ol>
     *
     * <p>If the column does not exist, or the column is full, no message will be sent to a message will be sent to {@value TOPIC_MOVES}. Instead  a
     * message will be sent to {@value QUEUE_ILLEGAL_MOVE} of an {@code IllegalMove} object, containing details of the illegal move. This message
     * should be ignored by the player who did not make the illegal move.
     * </p>
     *
     * @param column
     *            the column to make the move in.
     */
    @MessageMapping(ENDPOINT_MOVE)
    public void move(@Payload int column) {
        logger.info("/move endpoint called with payload '{}' ", column);

        try {
            gameState.addDisc(column, gameState.getPlayerToMove().getId());
        } catch (IllegalMoveException exception) {
            logger.debug("Illegal move by playerId={}: {}", exception.getIllegalMove().getPlayerId(), exception.getIllegalMove().getMessage());
            messagingTemplate.convertAndSend(QUEUE_ILLEGAL_MOVE, exception.getIllegalMove());
            return;
        }

        if (gameState.getStatus().equals(GameStatus.IN_PROGRESS)) {
            Player playerToMove = gameState.getPlayerToMove().equals(playerOne) ? playerTwo : playerOne;
            gameState.setPlayerToMove(playerToMove);
        }

        logger.debug("Move completed, with game state'{}' ", gameState.getStatus());
        logger.debug("Game grid: {}", Arrays.deepToString(gameState.getGrid()));
        messagingTemplate.convertAndSend(TOPIC_MOVES, gameState);
    }

    /**
     * Handles unexpected server exceptions and sends a message to {@value TOPIC_DISCONNECTED}. The client should then discontinue the game.
     *
     * @param exception
     *            the exception thrown up by the server.
     */
    @MessageExceptionHandler
    @SendToUser(destinations = QUEUE_ILLEGAL_MOVE, broadcast = false)
    public void handleException(Exception exception) {
        logger.error("There was an internal server error: {}", exception);
        messagingTemplate.convertAndSend(TOPIC_DISCONNECTED, "There was an internal server error: " + exception);
        resetGame();
    }

    /**
     * Handles a player disconnecting from the gameand sends a message to {@value TOPIC_DISCONNECTED}. The other client should then discontinue the
     * game.
     *
     * @param event
     *            the disconnection {@code SessionDisconnectEvent}.s
     */
    @EventListener
    public void onPlayerDisconnected(SessionDisconnectEvent event) {
        if (playerOne != null) {
            logger.info("A WebSocket session has been disconnected");
            messagingTemplate.convertAndSend("/topic/disconnected", "The other player has disconnected. Game over.");
            resetGame();
        }
    }

    private void resetGame() {
        logger.debug("Resetting game");
        gameState = new GameState(NUM_ROWS, NUM_COLUMNS, IN_A_ROW_TO_WIN);
        playerId = new AtomicInteger(1);
        playerOne = null;
        playerTwo = null;
    }
}
