package psimulator.userInterface.Editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.MouseActionListeners.DrawPanelListenerStrategy;
import psimulator.userInterface.Editor.MouseActionListeners.DrawPanelListenerStrategyAddCable;
import psimulator.userInterface.Editor.MouseActionListeners.DrawPanelListenerStrategyAddHwComponent;
import psimulator.userInterface.Editor.MouseActionListeners.DrawPanelListenerStrategyHand;
import psimulator.userInterface.Editor.Actions.ActionOnDelete;
import psimulator.userInterface.Editor.Components.AbstractComponent;
import psimulator.userInterface.Editor.Components.AbstractHwComponent;
import psimulator.userInterface.Editor.Enums.MainTool;
import psimulator.userInterface.MainWindowInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class DrawPanel extends JPanel implements Observer, ToolChangeInterface {
    // mouse listeners
    private DrawPanelListenerStrategy mouseListenerHand;
    private DrawPanelListenerStrategy mouseListenerAddHwComponent;
    private DrawPanelListenerStrategy mouseListenerCable;
    private DrawPanelListenerStrategy currentMouseListener;
    // END mouse listenrs
    private Graph graph = new Graph();
    private UndoManager undoManager = new UndoManager();
    private ZoomManager zoomManager = new ZoomManager();
    private AbstractImageFactory imageFactory;
    private MainWindowInterface mainWindow;
    // variables for creating cables
    private boolean lineInProgress = false;
    private Point lineStart;
    private Point lineEnd;
    // variables for marking components with transparent rectangle
    private boolean rectangleInProgress = false;
    private Rectangle rectangle;
    
    //
    private List<AbstractComponent> markedCables = new ArrayList<AbstractComponent>();
    private List<AbstractComponent> markedComponents = new ArrayList<AbstractComponent>();
    private Dimension defaultZoomAreaMin = new Dimension(800, 600);
    private Dimension defaultZoomArea = new Dimension(defaultZoomAreaMin);
    private Dimension actualZoomArea = new Dimension(defaultZoomArea);
    
    private DataLayerFacade dataLayer;
           

    public DrawPanel(MainWindowInterface mainWindow, AbstractImageFactory imageFactory, DataLayerFacade dataLayer) {
        super();

        this.mainWindow = mainWindow;
        this.imageFactory = imageFactory;
        this.dataLayer = dataLayer;

        actualZoomArea.width = zoomManager.doScaleToActual(defaultZoomArea.width);
        actualZoomArea.height = zoomManager.doScaleToActual(defaultZoomArea.height);
        
        this.setPreferredSize(actualZoomArea);
        this.setMinimumSize(actualZoomArea);
        this.setMaximumSize(actualZoomArea);

        this.setBackground(ColorMixerSignleton.drawPanelColor);

        createDrawPaneMouseListeners();

        // set mouse listener
        //setMouseListener(MainTool.HAND);
        //setHandTool();

        // add key binding for delete
        mainWindow.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("DELETE"), "DELETE");
        mainWindow.getRootPane().getActionMap().put("DELETE", new ActionOnDelete(graph, undoManager, this, mainWindow));

        zoomManager.addObserver(this);
    }

    /**
     * Reaction to notification from zoom manager
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        //set new sizes of this (JDrawPanel)
        actualZoomArea.width = zoomManager.doScaleToActual(defaultZoomArea.width);
        actualZoomArea.height = zoomManager.doScaleToActual(defaultZoomArea.height);

        this.setSize(actualZoomArea);
        this.setPreferredSize(actualZoomArea);
        this.setMinimumSize(actualZoomArea);
        this.setMaximumSize(actualZoomArea);
    }

    /**
     * Updates size of draw panel after graph change
     */
    public void updateSizeToFitComponents() {
        // find max X and max Y point in components
        int maxX = 0;
        int maxY = 0;

        for (AbstractHwComponent c : graph.getHwComponents()) {
            Point p = c.getLowerRightCornerLocation();
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
            //System.out.println("maxx = "+maxX + ", maxy = "+maxY);
        }

        // validate if new size is not smaller than defaultZoomAreaMin
        if (zoomManager.doScaleToDefault(maxX) < defaultZoomAreaMin.getWidth()
                && zoomManager.doScaleToDefault(maxY) < defaultZoomAreaMin.getHeight()) {
            // new size is smaller than defaultZoomAreaMin
            // set default zoom to default zoom min
            defaultZoomArea.setSize(defaultZoomAreaMin.width, defaultZoomAreaMin.height); 
            
            // set area according to defaultZoomArea
            actualZoomArea.setSize(zoomManager.doScaleToActual(defaultZoomArea.width), 
                    zoomManager.doScaleToActual(defaultZoomArea.height));
        } else {
            // update area size
            actualZoomArea.setSize(maxX, maxY);
            // update default zoom size
            defaultZoomArea.setSize(zoomManager.doScaleToDefault(actualZoomArea.width),
                    zoomManager.doScaleToDefault(actualZoomArea.height));
        }

        //System.out.println("area update");


        // let scrool pane in editor know about the change
        this.revalidate();
    }

    /**
     * Updates size of panel according to parameter if lowerRightCorner is
     * placed out of panel
     * @param rightDownCorner 
     */
    public void updateSize(Point lowerRightCorner) {
        // if nothing to resize
        if (!(lowerRightCorner.x > actualZoomArea.width || lowerRightCorner.y > actualZoomArea.height)) {
            return;
        }

        // if lowerRightCorner.x is out of area
        if (lowerRightCorner.x > actualZoomArea.width) {
            // update area width
            actualZoomArea.width = lowerRightCorner.x;
        }

        // if lowerRightCorner.y is out of area
        if (lowerRightCorner.y > actualZoomArea.height) {
            // update area height
            actualZoomArea.height = lowerRightCorner.y;
        }

        // update default zoom size
        defaultZoomArea.setSize(zoomManager.doScaleToDefault(actualZoomArea.width),
                zoomManager.doScaleToDefault(actualZoomArea.height));

        // let scrool pane in editor know about the change
        this.revalidate();
    }

    /**
     * Sets that cable is being paint
     * @param lineInProgres
     * @param start
     * @param end 
     */
    public void setLineInProgras(boolean lineInProgres, Point start, Point end) {
        this.lineInProgress = lineInProgres;
        lineStart = start;
        lineEnd = end;
    }
    
    
    public void setTransparetnRectangleInProgress(boolean rectangleInProgress, Rectangle rectangle){
        this.rectangleInProgress = rectangleInProgress;
        this.rectangle = rectangle;
    }

    public AbstractImageFactory getImageFactory() {
        return imageFactory;
    }


    public Graph getGraph() {
        return graph;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // set antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // paint line that is being currently made
        if (lineInProgress) {
            g2.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
        }

        // DRAW cables
        markedCables.clear();
        for (AbstractComponent c : graph.getBundlesOfCables()) {
            if (!c.isMarked()) {
                //g2.draw(c);
                c.paint(g2);
            } else {
                markedCables.add(c);
            }
        }
        for (AbstractComponent c : markedCables) {
            c.paint(g2);
        }


        // DRAW HWcomponents
        markedComponents.clear();
        for (AbstractComponent c : graph.getHwComponents()) {
            if (!c.isMarked()) {
                c.paint(g2);
            } else {
                markedComponents.add(c);
            }
        }
        for (AbstractComponent c : markedComponents) {
            c.paint(g2);
        }
        
        
        // DRAW makring rectangle
        if(rectangleInProgress){
            g2.setColor(Color.BLUE);
            g2.draw(rectangle);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.fill(rectangle);
            
        }
    }

    
    /**
     * Creates mouse listeners for all tools
     */
    protected final void createDrawPaneMouseListeners() {
        mouseListenerHand = new DrawPanelListenerStrategyHand(this, undoManager, zoomManager, mainWindow, dataLayer);
        mouseListenerAddHwComponent = new DrawPanelListenerStrategyAddHwComponent(this, undoManager, zoomManager, mainWindow, dataLayer);
        mouseListenerCable = new DrawPanelListenerStrategyAddCable(this, undoManager, zoomManager, mainWindow, dataLayer);
    }
    
    // ------ IMPLEMENTATION OF TOOL CHANGE INTERFACE
    @Override
    public void removeCurrentMouseListener(){
        if(currentMouseListener != null){
            currentMouseListener.deInitialize();
        }
        
        this.removeMouseListener(currentMouseListener);
        this.removeMouseMotionListener(currentMouseListener);
        this.removeMouseWheelListener(currentMouseListener);
    }
    
    @Override
    public DrawPanelListenerStrategy getMouseListener(MainTool tool){
        switch (tool) {
            case HAND:
                return mouseListenerHand;
            case ADD_CABLE:
                return mouseListenerCable;
            case ADD_REAL_PC:
            case ADD_END_DEVICE:
            case ADD_SWITCH:
            case ADD_ROUTER:    
                return mouseListenerAddHwComponent;
            
        }
        
        // this should never happen
        System.out.println("chyba v DrawPanel metoda getMouseListener(MainTool tool)");
        return mouseListenerHand;
    }
    
    @Override
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener){
        currentMouseListener = mouseListener;
        
        this.addMouseListener(currentMouseListener);
        this.addMouseMotionListener(currentMouseListener);
        this.addMouseWheelListener(currentMouseListener);
    }
  
    // END------ IMPLEMENTATION OF TOOL CHANGE INTERFACE
    
    /*
    @Override
    public void setToolChanged(AbstractTool tool) {
        if(currentMouseListener != null){
            currentMouseListener.deInitialize();
        }
        
        this.removeMouseListener(currentMouseListener);
        this.removeMouseMotionListener(currentMouseListener);
        this.removeMouseWheelListener(currentMouseListener);

        switch (tool.getTool()) {
            case HAND:
                currentMouseListener = mouseListenerHand;
                break;
            case ADD_REAL_PC:
            case ADD_END_DEVICE:
            case ADD_SWITCH:
            case ADD_ROUTER:    
                currentMouseListener = mouseListenerAddHwComponent;
                //currentMouseListener.setTool();
                
                break;
            case ADD_CABLE:
                currentMouseListener = mouseListenerCable;
                break;
        }

        this.addMouseListener(currentMouseListener);
        this.addMouseMotionListener(currentMouseListener);
        this.addMouseWheelListener(currentMouseListener);
    }
     * 
     */
    
    protected UndoManager getUndoManager() {
        return undoManager;
    }

    protected ZoomManager getZoomManager() {
        return zoomManager;
    }

    
}
