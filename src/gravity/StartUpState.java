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

	Button buttonSelected = null;
	int buttonSelectedIndex = 0;
	Button buttons[];
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		connectButton = new Button(440, 480, ResourceManager.getImage(GravGame.CONNECT_BUTTON_IMG_RSC));
		exitButton = new Button(440, 630, ResourceManager.getImage(GravGame.EXIT_BUTTON_IMG_RSC));
		buttons = new Button[] {connectButton, exitButton};
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		gg = (GravGame) game;
		input = container.getInput();
		container.setSoundOn(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceManager.getImage(GravGame.MAIN_MENU_BACKGROUND_IMG_RSC), 0, 0);
		//g.drawString("Press 1 to enter", 10, 30);
		connectButton.draw(g);
		exitButton.draw(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		if(connectButton.isMousePressing(input)) {
			gg.startServerHandler();
			gg.gameObjects.put(gg.playerID, new Vehicle(5.5f, 5.5f, gg.playerID));
			if(gg.playerID == 1) {
				System.out.println("player " + gg.playerID + " entering level select...");
				gg.enterState(GravGame.LEVELSELECT);
			}
			else {
				System.out.println("player " + gg.playerID + " entering wait screen...");
				gg.enterState(GravGame.WAITSCREEN);
			}
		}

		if(exitButton.isMousePressing(input)) {
			System.exit(0);
		}

		if(input.isKeyPressed(Input.KEY_DOWN) || input.isKeyPressed(Input.KEY_LEFT)) {
			if(buttonSelected != null) buttonSelected.toggleSelected();
			buttonSelectedIndex = Math.abs(buttonSelectedIndex - 1) % buttons.length;
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
			if(buttonSelected.equals(connectButton)) {
				gg.startServerHandler();
				gg.gameObjects.put(gg.playerID, new Vehicle(5.5f, 5.5f, gg.playerID));
				if(gg.playerID == 1) {
					System.out.println("player " + gg.playerID + " entering level select...");
					gg.enterState(GravGame.LEVELSELECT);
				}
				else {
					System.out.println("player " + gg.playerID + " entering wait screen...");
					gg.enterState(GravGame.WAITSCREEN);
				}
			}
			if(buttonSelected.equals(exitButton)) {
				System.exit(0);
			}
		}
	}

	@Override
	public int getID() {
		return GravGame.STARTUPSTATE;
	}
	
}