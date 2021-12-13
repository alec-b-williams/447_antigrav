package gravity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
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
class StartUpState extends BasicGameState {

	GravGame gg;
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		gg = (GravGame) game;
		container.setSoundOn(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		
		g.drawString("Press 1 to enter", 10, 30);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();

		if (input.isKeyDown(Input.KEY_1)) {
			gg.startServerHandler();
			gg.gameObjects.put(gg.playerID, new Vehicle(5.5f, 5.5f, gg.playerID));
			gg.enterState(GravGame.PLAYINGSTATE);
		}
	}

	@Override
	public int getID() {
		return GravGame.STARTUPSTATE;
	}
	
}