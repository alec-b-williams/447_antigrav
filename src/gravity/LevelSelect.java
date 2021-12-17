package gravity;

import jig.ResourceManager;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state is active prior to the Game starting. In this state, sound is
 * turned off, and the bounce counter shows '?'. The user can only interact with
 * the game by pressing the SPACE key which transitions to the Playing State.
 * Otherwise, all game objects are rendered and updated normally.
 *
 * Transitions From (Initialization), GameOverState
 *
 * Transitions To PlayingState
 */
class LevelSelect extends BasicGameState {

    GravGame gg;
    Input input;
    Button level1Button;
    Button level2Button;
    Button level3Button;

    Button buttonSelected = null;
    int buttonSelectedIndex = 0;
    Button buttons[];

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        level1Button = new Button(100, 362, ResourceManager.getImage(GravGame.LEVEL1_BUTTON_IMG_RSC));
        level2Button = new Button(490, 362, ResourceManager.getImage(GravGame.LEVEL2_BUTTON_IMG_RSC));
        level3Button = new Button(880, 362, ResourceManager.getImage(GravGame.LEVEL3_BUTTON_IMG_RSC));
        buttons = new Button[] {level1Button, level2Button, level3Button};
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {
        gg = (GravGame) game;
        input = container.getInput();
        container.setSoundOn(false);
        System.out.println("In level select...");
    }


    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(ResourceManager.getImage(GravGame.LEVEL_SELECT_BACKGROUND_IMG_RSC), 0, 0);
        level1Button.draw(g);
        level2Button.draw(g);
        level3Button.draw(g);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if(level1Button.isMousePressing(input)) {
            gg.p1Start(0);
            gg.enterState(GravGame.PLAYINGSTATE);
        }
        if(level2Button.isMousePressing(input)) {
            gg.p1Start(1);
            gg.enterState(GravGame.PLAYINGSTATE);
        }
        if(level3Button.isMousePressing(input)) {
            gg.p1Start(2);
            gg.enterState(GravGame.PLAYINGSTATE);
        }

        if(input.isKeyPressed(Input.KEY_DOWN) || input.isKeyPressed(Input.KEY_LEFT)) {
            if(buttonSelected != null) buttonSelected.toggleSelected();
            buttonSelectedIndex = ((((buttonSelectedIndex - 1) % buttons.length) + buttons.length) % buttons.length);
            buttonSelected = buttons[buttonSelectedIndex];
            buttonSelected.toggleSelected();
        }

        if(input.isKeyPressed(Input.KEY_UP) || input.isKeyPressed(Input.KEY_RIGHT)) {
            if(buttonSelected != null) buttonSelected.toggleSelected();
            buttonSelectedIndex = (buttonSelectedIndex + 1) % buttons.length;
            buttonSelected = buttons[buttonSelectedIndex];
            buttonSelected.toggleSelected();
        }

        if(input.isKeyPressed(Input.KEY_ENTER) && buttonSelected != null) {
            if(buttonSelected.equals(level1Button)) {
                gg.p1Start(0);
                gg.enterState(GravGame.PLAYINGSTATE);
            }
            if(buttonSelected.equals(level2Button)) {
                gg.p1Start(1);
                gg.enterState(GravGame.PLAYINGSTATE);
            }
            if(buttonSelected.equals(level3Button)) {
                gg.p1Start(2);
                gg.enterState(GravGame.PLAYINGSTATE);
            }
        }
    }

    @Override
    public int getID() {
        return GravGame.LEVELSELECT;
    }

}