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
class StartUpState extends BasicGameState {

	GravGame gg;
	Input input;
	Button connectButton;
	Button exitButton;
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		connectButton = new Button(440, 480, ResourceManager.getImage(GravGame.CONNECT_BUTTON_IMG_RSC));
		exitButton = new Button(440, 630, ResourceManager.getImage(GravGame.EXIT_BUTTON_IMG_RSC));
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		gg = (GravGame) game;
		input = container.getInput();
		container.setSoundOn(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		g.drawImage(ResourceManager.getImage(GravGame.MAIN_MENU_BACKGROUND_IMG_RSC), 0, 0);
		//g.drawString("Press 1 to enter", 10, 30);
		connectButton.draw(g);
		exitButton.draw(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		if(connectButton.isMousePressing(input)) {
			gg.startServerHandler();
			gg.gameObjects.put(gg.playerID, new Vehicle(5.5f, 5.5f, gg.playerID));
			gg.enterState(GravGame.PLAYINGSTATE);
		}

		if(exitButton.isMousePressing(input)) {
			System.exit(0);
		}

		//if (input.isKeyDown(Input.KEY_1)) {
		//	gg.startServerHandler();
		//	gg.gameObjects.put(gg.playerID, new Vehicle(5.5f, 5.5f, gg.playerID));
		//	gg.enterState(GravGame.PLAYINGSTATE);
		//}
	}

	@Override
	public int getID() {
		return GravGame.STARTUPSTATE;
	}
	
}