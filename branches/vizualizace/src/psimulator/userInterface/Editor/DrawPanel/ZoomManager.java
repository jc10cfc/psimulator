package psimulator.userInterface.Editor.DrawPanel;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Observable;

/**
 *
 * @author Martin
 */
public class ZoomManager extends Observable {

    private int width = 140;
    private double defaultScale = 0.8;
    private double scale = defaultScale;
    private double zoomInc = 0.1;
    private double minScale = 0.1;
    private double maxScale = 1.0;
    private float basicStrokeWidth = 5f;

    /**
     * returns Icon size according to scale and default icon size
     * @return 
     */
    public int getIconWidth() {
        return (int) (getCurrentScale() * width);
    }

    /**
     * returns Icon size according to default zoom  scale
     */
    public int getIconWidthDefaultZoom() {
        return (int) (width);
    }

    /**
     * returns stroke width according to current scale
     * @return 
     */
    public float getStrokeWidth() {
        return Math.max((float) (basicStrokeWidth * getCurrentScale()), 0.8f);
    }

    /**
     * Gets current scale
     * @return Current scale
     */
    public double getCurrentScale() {
        return scale;
    }

    /**
     * Zooms in if possible and notifies all observers
     */
    public void zoomIn() {
        zoomIn(new Point(0, 0));
    }

    public void zoomIn(Point mousePostition) {
        if (canZoomIn()) {
            double tmp = scale;
            scale += 1 * zoomInc;
            tmp = tmp / scale;
            // notify all observers
            notifyAllObservers();
        }
    }

    /**
     * Zooms out if possible and notifies all observers
     */
    public void zoomOut() {
        zoomOut(new Point(0, 0));
    }

    /**
     * Zooms out if possible and notifies all observers
     */
    public void zoomOut(Point mousePostition) {
        if (canZoomOut()) {
            double tmp = scale;
            scale += -1 * zoomInc;
            tmp = tmp / scale;
            // notify all observers
            notifyAllObservers();
        }
    }

    /**
     * Resets zoom to default and notifies all observers
     */
    public void zoomReset() {
        // scale set to default
        scale = defaultScale;

        // notify all observers
        notifyAllObservers();
    }

    /**
     * Finds whether is possible to zoom out
     * @return true if possible, otherwise false
     */
    public boolean canZoomOut() {
        // if maximum zoom not reached
        if (scale > minScale + zoomInc) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds whether is possible to zoom in
     * @return true if possible, otherwise false
     */
    public boolean canZoomIn() {
        // if maximum zoom reached
        if (scale < maxScale - zoomInc) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Scales point in default scale to actual scale point
     * @param defaultScalePoint Point in default scale
     * @return Scaled point in actualScale
     */
    public Point doScaleToActual(Point defaultScalePoint) {
        return new Point((int) (defaultScalePoint.x * scale), (int) (defaultScalePoint.y * scale));
    }

    /**
     * Scales dimension in default scale to actual scale point
     * @param defaultScaleDimension Dimension in actual scale
     * @return Scaled dimension in actual scale
     */
    public Dimension doScaleToActual(Dimension defaultScaleDimension) {
        return new Dimension((int) (defaultScaleDimension.width * scale), (int) (defaultScaleDimension.height * scale));
    }

    /**
     * Scales defaultScale number to actualScale number
     * @param defaultScale Number in default scale
     * @return Number in actual scale
     */
    public int doScaleToActual(int defaultScale) {
        return ((int) (defaultScale * scale));
    }

    /**
     * Scales point in actual scale to default scale point
     * @param actualScalePoint Point in actual scale
     * @return Scaled point in default scale
     */
    public Point doScaleToDefault(Point actualScalePoint) {
        return new Point((int) (actualScalePoint.x / scale), (int) (actualScalePoint.y / scale));
    }

    /**
     * Scales dimension in actual scale to default scale point
     * @param actualScaleDimension Dimension in actual scale
     * @return Scaled dimension in default scale
     */
    public Dimension doScaleToDefault(Dimension actualScaleDimension) {
        return new Dimension((int) (actualScaleDimension.width / scale), (int) (actualScaleDimension.height / scale));
    }

    /**
     * Scales actualScale number todefaultScale number
     * @param actualScale Number in actual scale 
     * @return Number in default scale
     */
    public int doScaleToDefault(int actualScale) {
        return ((int) (actualScale / scale));
    }

    /**
     * calls setChanged and notifyObservers
     */
    private void notifyAllObservers() {
        setChanged();
        notifyObservers(new ZoomEventWrapper(false, 0, 0, 0.0));
    }
}
