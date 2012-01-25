package psimulator.userInterface.SimulatorEditor.DrawPanel;

/**
 *
 * @author Martin
 */
public class ZoomEventWrapper {
    private boolean scaleToMousePosition;
    private int mouseX;
    private int mouseY;
    private double newZoomDivOld;

    public ZoomEventWrapper(boolean scaleToMousePosition, int mouseX, int mouseY, double newZoomDivOld) {
        this.scaleToMousePosition = scaleToMousePosition;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.newZoomDivOld = newZoomDivOld;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public boolean isScaleToMousePosition() {
        return scaleToMousePosition;
    }

    public double getNewZoomDivOld() {
        return newZoomDivOld;
    }
    
    
}
