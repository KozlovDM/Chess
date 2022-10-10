package UI.screen;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;

import java.awt.event.KeyEvent;

public class MenuScreen extends Screen {
    public static final String NAME = "MENUSCREEN";

    private ImageComponent start;
    private ImageComponent exit;


    public MenuScreen() {
        super(NAME);
    }

    @Override
    protected void initializeComponents() {
        double buttonX = Game.window().getWidth() / 2.0D;
        double buttonY = Game.window().getHeight() * 7.0D / 16.0D;
        double buttonWidth = Game.window().getWidth() * 3.0D / 16.0D;
        double buttonHeight = Game.window().getHeight() * 1.0D / 16.0D;
        double buttonPadding = Game.window().getHeight() * 1.0D / 32.0D;

        this.start = new ImageComponent(buttonX - buttonWidth / 2.0D, buttonY, buttonWidth, buttonHeight, "1 Белые");
        this.exit = new ImageComponent(buttonX - buttonWidth / 2.0D, buttonY + buttonHeight + buttonPadding,
                buttonWidth, buttonHeight, "2 Черные");

        Input.keyboard().onKeyTyped(callback -> {
            if (KeyEvent.VK_1 == callback.getKeyCode()) {
                GameManager.side = 0;
                GameManager.setState(GameManager.GameState.INGAME);
            } else if (KeyEvent.VK_2 == callback.getKeyCode()) {
                GameManager.side = 1;
                GameManager.setState(GameManager.GameState.INGAME);
            }
        });

        this.getComponents().add(this.start);
        this.getComponents().add(this.exit);
    }
}
