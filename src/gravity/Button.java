package gravity;

import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

public class Button {

    public Image buttonImage;
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;
    public int width;
    public int height;

    public Button(int x, int y, Image image) {
        buttonImage = image;
        minX = x;
        minY = y;
        width = image.getWidth();
        height = image.getHeight();
        maxX = x + width;
        maxY = y + height;
    }

    public boolean isMouseTouching(Input input) {
        int x = input.getMouseX();
        int y = input.getMouseY();

        if((x >= minX && x <= maxX) && (y >= minY && y <= maxY)) {
            buttonImage.setImageColor(255, 255, 255, 0.75f);
            return true;
        }
        buttonImage.setImageColor(255, 255, 255, 1f);
        return false;
    }

    public boolean isMousePressing(Input input) {
        return isMouseTouching(input) && input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
    }

    public void draw(Graphics g) {
        g.drawImage(buttonImage, minX, minY);
    }
}
