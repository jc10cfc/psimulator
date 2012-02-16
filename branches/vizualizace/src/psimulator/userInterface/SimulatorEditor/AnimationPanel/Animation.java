package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 *
 * @author Martin
 */
public class Animation{
    
    private int defaultZoomStartX;
    private int defaultZoomStartY;
    
    private Image image;
    
    private int x;
    private int y;
    
    private int maxX;
    private int maxY;
    
    private boolean visible;
    
    private final int ANIMATION_SPEED = 2;
    
    
    public Animation(){
        ImageIcon ii = new ImageIcon(this.getClass().getResource("/resources/toolbarIcons/editor_toolbar/cursor_hand_mod_2.png"));
        image = ii.getImage();
        
        Random r = new Random();
        
        x = r.nextInt(300);
        y = x;
        
        maxX = r.nextInt(200)+300;
        maxY = maxX;
    }
    
    
    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void move() {
        x += ANIMATION_SPEED;
        y += ANIMATION_SPEED;

        if (y > maxY) {
            y = -45;
            x = -45;
        }
    }
    
}
