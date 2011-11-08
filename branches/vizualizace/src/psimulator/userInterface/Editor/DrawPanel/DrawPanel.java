package psimulator.userInterface.Editor.DrawPanel;

import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.ColorMixerSignleton;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Actions.ActionAlignComponentsToGrid;
import psimulator.userInterface.Editor.DrawPanel.Actions.ActionFitToSize;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategyAddCable;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategyAddHwComponent;
import psimulator.userInterface.Editor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategyHand;
import psimulator.userInterface.Editor.DrawPanel.Actions.ActionOnDelete;
import psimulator.userInterface.Editor.DrawPanel.Actions.ActionSelectAll;
import psimulator.userInterface.Editor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.Editor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.Editor.DrawPanel.Graph.GraphOuterInterface;
import psimulator.userInterface.Editor.EditorInnerInterface;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public final class DrawPanel extends DrawPanelOuterInterface implements 
        DrawPanelInnerInterface, Observer, DrawPanelSizeChangeInnerInterface {
    // mouse listeners

    private DrawPanelListenerStrategy mouseListenerHand;
    private DrawPanelListenerStrategy mouseListenerAddHwComponent;
    private DrawPanelListenerStrategy mouseListenerCable;
    private DrawPanelListenerStrategy currentMouseListener;
    // END mouse listenrs
    private Graph graph;
    private UndoManager undoManager = new UndoManager();
    private ZoomManager zoomManager = new ZoomManager();
    private AbstractImageFactory imageFactory;
    private MainWindowInnerInterface mainWindow;
    private EditorInnerInterface editorPanel;
    // variables for creating cables
    private boolean lineInProgress = false;
    private Point lineStartInDefaultZoom;
    private Point lineEndInActualZoom;
    // variables for marking components with transparent rectangle
    private boolean rectangleInProgress = false;
    private Rectangle rectangle;
    //
    private Dimension defaultZoomAreaMin = new Dimension(800, 600);
    private Dimension defaultZoomArea = new Dimension(defaultZoomAreaMin);
    private Dimension actualZoomArea = new Dimension(defaultZoomArea);
    
    private DataLayerFacade dataLayer;
    
    private EnumMap<DrawPanelAction, AbstractAction> actions;

    public DrawPanel(MainWindowInnerInterface mainWindow, EditorInnerInterface editorPanel, AbstractImageFactory imageFactory, DataLayerFacade dataLayer) {
        super();

        this.editorPanel = editorPanel;
        this.mainWindow = mainWindow;
        this.imageFactory = imageFactory;
        this.dataLayer = dataLayer;

        //this.graph = new Graph((DrawPanelSizeChangeInnerInterface)this, zoomManager);
 
        actualZoomArea.width = zoomManager.doScaleToActual(defaultZoomArea.width);
        actualZoomArea.height = zoomManager.doScaleToActual(defaultZoomArea.height);

        this.setPreferredSize(actualZoomArea);
        this.setMinimumSize(actualZoomArea);
        this.setMaximumSize(actualZoomArea);

        this.setBackground(ColorMixerSignleton.drawPanelColor);

        createDrawPaneMouseListeners();
        createAllActions();
        
        createKeyBindings();

        zoomManager.addObserver((Observer)this);
    }
    
    private void createKeyBindings(){
        InputMap inputMap = mainWindow.getRootPane().getInputMap();
        ActionMap actionMap = mainWindow.getRootPane().getActionMap();
      
        
        
        // add key binding for delete
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        
        inputMap.put(key, null);
        actionMap.put(DrawPanelAction.DELETE, null);
        
        inputMap.put(key, DrawPanelAction.DELETE);
        actionMap.put(DrawPanelAction.DELETE, getAbstractAction(DrawPanelAction.DELETE));
    }
    
    /**
     * creates all actions according to DrawPanelAction Enum
     */
    private void createAllActions(){
        actions = new EnumMap<DrawPanelAction, AbstractAction>(DrawPanelAction.class);
        
        for(DrawPanelAction drawPanelAction : DrawPanelAction.values()){
            switch(drawPanelAction){
                case ALIGN_COMPONENTS_TO_GRID:
                    actions.put(drawPanelAction, new ActionAlignComponentsToGrid(undoManager, this, mainWindow));
                    break;
                case DELETE:
                    actions.put(drawPanelAction, new ActionOnDelete(undoManager, this, mainWindow));
                    break;
                case PROPERTIES:
                    break;
                case FIT_TO_SIZE:
                    actions.put(drawPanelAction, new ActionFitToSize(undoManager, this, mainWindow));
                    break;
                case SELECT_ALL:
                    actions.put(drawPanelAction, new ActionSelectAll(undoManager, this, mainWindow));
                    break;
            }
        }
    }
    
    /**
     * Creates mouse listeners for all tools
     */
    private void createDrawPaneMouseListeners() {
        mouseListenerHand = new DrawPanelListenerStrategyHand(this, undoManager, zoomManager, mainWindow, dataLayer);
        mouseListenerAddHwComponent = new DrawPanelListenerStrategyAddHwComponent(this, undoManager, zoomManager, mainWindow, dataLayer);
        mouseListenerCable = new DrawPanelListenerStrategyAddCable(this, undoManager, zoomManager, mainWindow, dataLayer);
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
            Stroke stroke = new BasicStroke(zoomManager.getStrokeWidth());
            Stroke tmp = g2.getStroke();
            g2.setStroke(stroke);
            
            g2.drawLine(zoomManager.doScaleToActual(lineStartInDefaultZoom.x), 
                    zoomManager.doScaleToActual(lineStartInDefaultZoom.y),
                    lineEndInActualZoom.x, 
                    lineEndInActualZoom.y);
            
            g2.setStroke(tmp);
        }

        if(graph!=null){
            graph.paint(g2);
        }
        
        
        // DRAW makring rectangle
        if (rectangleInProgress) {
            g2.setColor(Color.BLUE);
            g2.draw(rectangle);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.fill(rectangle);

        }
    }

// ========  IMPLEMENTATION OF DrawPanelSizeChangeInnerInterface ==========   
    
    @Override
    public void updateSize(Dimension dim) {
        // if nothing to resize
        if (!(dim.width > actualZoomArea.width || dim.height > actualZoomArea.height)) {
            return;
        }

        // if lowerRightCorner.x is out of area
        if (dim.width > actualZoomArea.width) {
            // update area width
            actualZoomArea.width = dim.width;
        }

        // if lowerRightCorner.y is out of area
        if (dim.height > actualZoomArea.height) {
            // update area height
            actualZoomArea.height = dim.height;
        }

        // update default zoom size
        defaultZoomArea.setSize(zoomManager.doScaleToDefault(actualZoomArea.width),
                zoomManager.doScaleToDefault(actualZoomArea.height));

        // let scrool pane in editor know about the change
        this.revalidate();
    }
// END ========  IMPLEMENTATION OF DrawPanelSizeChangeInnerInterface ==========  

    
    
// ====================  IMPLEMENTATION OF Observer ======================   
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
// END ====================  IMPLEMENTATION OF Observer ======================      
    
    
// ================  IMPLEMENTATION OF ToolChangeInterface =================
    
    @Override
    public void removeCurrentMouseListener() {
        if (currentMouseListener != null) {
            currentMouseListener.deInitialize();
        }

        this.removeMouseListener(currentMouseListener);
        this.removeMouseMotionListener(currentMouseListener);
        this.removeMouseWheelListener(currentMouseListener);
    }

    @Override
    public DrawPanelListenerStrategy getMouseListener(MainTool tool) {
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
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener) {
        currentMouseListener = mouseListener;
        
        currentMouseListener.initialize();
        

        this.addMouseListener(currentMouseListener);
        this.addMouseMotionListener(currentMouseListener);
        this.addMouseWheelListener(currentMouseListener);
    }
// END ==============  IMPLEMENTATION OF ToolChangeInterface ===============
    
  
// ============== IMPLEMENTATION OF DrawPanelInnerInterface ================
    
    
    @Override
    public GraphOuterInterface getGraphOuterInterface() {
        return graph;
    }
    
    @Override
    public AbstractImageFactory getImageFactory() {
        return imageFactory;
    }
    
    @Override
    public void setLineInProgras(boolean lineInProgres, Point startInDefaultZoom, Point endInActualZoom) {
        this.lineInProgress = lineInProgres;
        lineStartInDefaultZoom = startInDefaultZoom;
        lineEndInActualZoom = endInActualZoom;
    }
    
    
    @Override
    public void setTransparetnRectangleInProgress(boolean rectangleInProgress, Rectangle rectangle) {
        this.rectangleInProgress = rectangleInProgress;
        this.rectangle = rectangle;
    }
    
    @Override
    public void doSetDefaultToolInEditorToolBar() {
        editorPanel.doSetDefaultToolInToolBar();
    }
    
// END ============ IMPLEMENTATION OF DrawPanelInnerInterface ==============
    
    
// ============== IMPLEMENTATION OF DrawPanelOuterInterface ================
    
    @Override
    public Graph removeGraph() {
        Graph tmp = graph;
        graph = null;
        
        undoManager.discardAllEdits();
        zoomManager.zoomReset();
        return tmp;
    }

    @Override
    public void setGraph(Graph graph) {
        if(this.graph != null){
            removeGraph();
        }
        
        this.graph = graph;
        graph.initialize(this, zoomManager);
    }
    
    @Override
    public boolean hasGraph() {
        if(graph == null){
            return false;
        }
        return true;
    }
    
    @Override
    public Graph getGraph() {
        return graph;
    }
    
    @Override
    public AbstractAction getAbstractAction(DrawPanelAction action){
        return actions.get(action);
    }
    
    @Override
    public boolean canUndo() {
        return undoManager.canUndo();
    }

    @Override
    public boolean canRedo() {
        return undoManager.canRedo();
    }

    @Override
    public void undo() {
        undoManager.undo();
    }

    @Override
    public void redo() {
        undoManager.redo();
    }

    @Override
    public boolean canZoomIn() {
        return zoomManager.canZoomIn();
    }

    @Override
    public boolean canZoomOut() {
        return zoomManager.canZoomOut();
    }

    @Override
    public void zoomIn() {
        zoomManager.zoomIn();
    }

    @Override
    public void zoomOut() {
        zoomManager.zoomOut();
    }

    @Override
    public void zoomReset() {
        zoomManager.zoomReset();
    }

    @Override
    public void addObserverToZoomManager(Observer obsrvr) {
        zoomManager.addObserver(obsrvr);
    }
    
    @Override
    public void doFitToGraphSize() {
        
        int graphWidthActual = graph.getWidth();
        int graphHeightActual = graph.getHeight();
                
        
        // validate if new size is smaller than defaultZoomAreaMin
        if (zoomManager.doScaleToDefault(graphWidthActual) < defaultZoomAreaMin.getWidth()
                && zoomManager.doScaleToDefault(graphHeightActual) < defaultZoomAreaMin.getHeight()) {
            // new size is smaller than defaultZoomAreaMin
            // set defaultZoomArea to defaultZoomAreaMin
            defaultZoomArea.setSize(defaultZoomAreaMin.width, defaultZoomAreaMin.height);

            // set area according to defaultZoomArea
            actualZoomArea.setSize(zoomManager.doScaleToActual(defaultZoomArea.width),
                    zoomManager.doScaleToActual(defaultZoomArea.height));
        } else {
            // update area size
            actualZoomArea.setSize(graphWidthActual, graphHeightActual);
            // update default zoom size
            defaultZoomArea.setSize(zoomManager.doScaleToDefault(actualZoomArea.width),
                    zoomManager.doScaleToDefault(actualZoomArea.height));
        }

        // let scrool pane in editor know about the change
        this.revalidate();
    }
    
// END ============ IMPLEMENTATION OF DrawPanelOuterInterface ==============

    

    
    
}

