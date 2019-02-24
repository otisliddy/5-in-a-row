package com.otisliddy.fiveinarow.controller;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.otisliddy.fiveinarow.config.SystemProperties;
import com.otisliddy.fiveinarow.exception.IllegalMove;
import com.otisliddy.fiveinarow.game.GameState;
import com.otisliddy.fiveinarow.game.GameStatus;
import com.otisliddy.fiveinarow.game.Player;
import com.otisliddy.fiveinarow.util.GenericFrameHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameControllerIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(GameControllerIntegrationTest.class);
    private static final String WEBSOCKET_URI = "ws://localhost:8080/5-in-a-row";
    private static final String ENDPOINT_START = "/fiveinarow/start";
    private static final String ENDPOINT_MOVE = "/fiveinarow/move";
    private static final String SUBSCRIBE_MOVES = "/topic/moves";
    private static final String SUBSCRIBE_START = "/user/queue/start";
    private static final String SUBSCRIBE_ILLEGAL_MOVE = "/queue/illegal-move";
    private static final String SUBSCRIBE_DISCONNECTED = "/topic/disconnected";

    private GenericFrameHandler<Player> playerResponse = new GenericFrameHandler<>(Player.class);
    private GenericFrameHandler<GameState> gameStateResponse = new GenericFrameHandler<>(GameState.class);
    private GenericFrameHandler<IllegalMove> illegalMoveResponse = new GenericFrameHandler<>(IllegalMove.class);
    private GenericFrameHandler<String> stringResponse = new GenericFrameHandler<>(String.class);
    final WebSocketStompClient stompClient =
               new WebSocketStompClient(new SockJsClient(asList(new WebSocketTransport(new StandardWebSocketClient()))));
    private StompSession stompSession;
    @Autowired
    private GameState gameStateBefore;

    @BeforeClass
    public static void setupClass() {
        System.setProperty(SystemProperties.NUM_ROWS.getName(), "3");
        System.setProperty(SystemProperties.NUM_COLUMNS.getName(), "4");
        System.setProperty(SystemProperties.IN_A_ROW_TO_WIN.getName(), "3");
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty(SystemProperties.NUM_ROWS.getName());
        System.clearProperty(SystemProperties.NUM_COLUMNS.getName());
        System.clearProperty(SystemProperties.IN_A_ROW_TO_WIN.getName());
    }

    @Before
    public void setup() throws InterruptedException, ExecutionException, TimeoutException {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
        }).get(5, SECONDS);

        stompSession.subscribe(SUBSCRIBE_START, playerResponse);
        stompSession.subscribe(SUBSCRIBE_MOVES, gameStateResponse);
        stompSession.subscribe(SUBSCRIBE_ILLEGAL_MOVE, illegalMoveResponse);
    }

    @After
    public void reset() {
        stompSession.disconnect();
    }

    @Test
    public void testStartEndpoint() {
        stompSession.send(ENDPOINT_START, "player 1");

        Player player = playerResponse.getRespoonse();
        assertNotNull(player);
        assertEquals(1, player.getId());
        assertEquals("player 1", player.getName());
        GameState gameState = gameStateResponse.getRespoonse();
        assertNull(gameState);

        stompSession.send(ENDPOINT_START, "player 2");

        player = playerResponse.getRespoonse();
        assertNotNull(player);
        assertEquals(2, player.getId());
        assertEquals("player 2", player.getName());
        gameState = gameStateResponse.getRespoonse();
        assertNotNull(gameState);
        assertEquals(1, gameState.getPlayerToMove().getId());
    }

    @Test
    public void testMoveEndpoint_initialState() {
        testStartEndpoint();
        stompSession.send(ENDPOINT_MOVE, 1);

        final GameState gameStateAfter = gameStateResponse.getRespoonse();
        assertNotNull(gameStateAfter);
        assertEquals(GameStatus.IN_PROGRESS, gameStateAfter.getStatus());
        assertEquals(1, gameStateAfter.getGrid()[0][0]);
    }

    @Test
    public void testMoveEndpoint_illegalColumn() {
        testStartEndpoint();
        stompSession.send(ENDPOINT_MOVE, 0);

        final GameState gameStateAfter = gameStateResponse.getRespoonse();
        assertNull(gameStateAfter);

        final IllegalMove illegalMove = illegalMoveResponse.getRespoonse();
        assertNotNull(illegalMove);
        assertEquals("Column 0 is out of range of (1-4)", illegalMove.getMessage());
    }

    @Test
    public void testMoveEndpoint_fullColumn() throws Exception {
        testStartEndpoint();
        gameStateBefore.addDisc(2, 1);
        gameStateBefore.addDisc(2, 2);
        gameStateBefore.addDisc(2, 1);

        stompSession.send(ENDPOINT_MOVE, 2);

        final GameState gameStateAfter = gameStateResponse.getRespoonse();
        assertNull(gameStateAfter);

        final IllegalMove illegalMove = illegalMoveResponse.getRespoonse();
        assertNotNull(illegalMove);
        assertEquals("No more discs may be added to column 2 because it is full", illegalMove.getMessage());
    }

    @Test
    public void testMoveEndpoint_gameWon() throws Exception {
        testStartEndpoint();
        gameStateBefore.addDisc(1, 1);
        gameStateBefore.addDisc(1, 1);

        stompSession.send(ENDPOINT_MOVE, 1);

        final GameState gameStateAfter = gameStateResponse.getRespoonse();
        assertNotNull(gameStateAfter);
        assertEquals(GameStatus.OVER_WON, gameStateAfter.getStatus());
        assertEquals(1, gameStateAfter.getPlayerToMove().getId());
    }

}
