package psimulator.userInterface.SimulatorEditor.DrawPanel;

/**
 *
 * @author Martin
 */
public class ZoomEventWrapper {
    
    private double oldScale;
    private double newScale;
    
    private int mouseXInOldZoom;
    private int mouseYInOldZoom;

    public ZoomEventWrapper(double oldScale, double newScale, int mouseXInOldZoom, int mouseYInOldZoom) {
        this.oldScale = oldScale;
        this.newScale = newScale;
        this.mouseXInOldZoom = mouseXInOldZoom;
        this.mouseYInOldZoom = mouseYInOldZoom;
    }

    public int getMouseXInOldZoom() {
        return mouseXInOldZoom;
    }

    public int getMouseYInOldZoom() {
        return mouseYInOldZoom;
    }

    public double getNewScale() {
        return newScale;
    }

    public double getOldScale() {
        return oldScale;
    }
    
    
    
            
    
    
    
    
    /*
    private boolean scaleToMousePosition;
    private int mouseX;
    private int mouseY;
    private double newZoomDivOld;
    private boolean zoomIn;
    
    
    

    public ZoomEventWrapper(boolean scaleToMousePosition, int mouseX, int mouseY, double newZoomDivOld) {
        this.scaleToMousePosition = scaleToMousePosition;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.newZoomDivOld = newZoomDivOld;
        //this.zoomIn = zoomIn;
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
    
    */

}
