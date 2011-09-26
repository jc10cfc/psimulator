package psimulator.userInterface.Editor;

/**
 *
 * @author Martin
 */
public class ZoomEventWrapper {
    private double scale;
    private int mouseX;
    private int mouseY;

    public ZoomEventWrapper(double scale, int mouseX, int mouseY) {
        this.scale = scale;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public double getScale() {
        return scale;
    }
    
}
