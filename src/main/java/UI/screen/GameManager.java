package UI.screen;

import AI.AI;
import engine.board.Position;
import de.gurkenlabs.litiengine.Game;

import java.util.concurrent.Executors;

public class GameManager {
    public enum GameState {
        INGAME, PAUSED, HELP, MENU;
    }

    public static Integer side;

    private static GameState state;


    public static void setState(GameState state) {

        switch (state) {
            case INGAME -> {
                startIngameState();
            }
            case MENU -> {
                startMenuState();

            }
        }
    }

    private static void startIngameState() {
        GameScreen game = (GameScreen) Game.screens().get(GameScreen.NAME);
        Position position = new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        game.setPosition(position);
        AI ai = new AI();
        Game.screens().display("GAMESCREEN");
        state = GameState.INGAME;
        Executors.newSingleThreadExecutor().execute(() ->
        {
            try {
                Position threadPosition = position;
                while (true) {
                    if (threadPosition.getSide() == side) {
                        while (threadPosition.getSide() == side) {
                            side = side;
                        }
                        threadPosition = game.getPosition();
                    } else {
                        var move = ai.bestMove(threadPosition, 5000);
                        if (move == null) {
                            return;
                        }
                        threadPosition.move(move);
                        game.updatePosition(threadPosition, move);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void startMenuState() {
        Game.screens().display("MENUSCREEN");
        state = GameState.MENU;
    }
}
