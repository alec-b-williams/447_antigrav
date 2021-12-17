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
class WaitScreen extends BasicGameState {

    GravGame gg;
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        gg = (GravGame) game;
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) {

    }


    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(ResourceManager.getImage(GravGame.WAITING_SCREEN_BACKGROUND_IMG_RSC), 0, 0);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        gg.notp1Start();
        gg.enterState(GravGame.PLAYINGSTATE);
    }

    @Override
    public int getID() {
        return GravGame.WAITSCREEN;
    }

}