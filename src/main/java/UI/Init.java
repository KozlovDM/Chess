package UI;

import UI.screen.GameManager;
import UI.screen.GameScreen;
import UI.screen.MenuScreen;
import de.gurkenlabs.litiengine.Game;

public class Init {
    public static void initUi() {
        Game.info().setName("Chess");
        Game.info().setVersion("v0.0.1");
        Game.init();
        Game.graphics().setBaseRenderScale(2.701f);

        Game.screens().add(new MenuScreen());
        Game.screens().add(new GameScreen());

        GameManager.setState(GameManager.GameState.MENU);
        Game.start();
    }
}
