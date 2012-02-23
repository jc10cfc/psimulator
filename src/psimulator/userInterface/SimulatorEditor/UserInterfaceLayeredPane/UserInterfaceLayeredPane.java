package psimulator.userInterface.SimulatorEditor.UserInterfaceLayeredPane;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanel;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class UserInterfaceLayeredPane extends UserInterfaceLayeredPaneOuterInterface implements Observer,
        UserInterfaceLayeredPaneInnerInterface{

    private DrawPanelOuterInterface jPanelDraw; // draw panel
    private AnimationPanelOuterInterface jPanelAnimation; // animation panel
    //
    //private ZoomManager zoomManager = new ZoomManager();
    //
    private MainWindowInnerInterface mainWindow;
    private UserInterfaceMainPanelInnerInterface userInterface;
    //
    private Dimension defaultZoomAreaMin = new Dimension(800, 600);
    private Dimension defaultZoomArea = new Dimension(defaultZoomAreaMin);
    private Dimension actualZoomArea = new Dimension(defaultZoomArea);

    public UserInterfaceLayeredPane(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface userInterface,
            DataLayerFacade dataLayer) {

        //
        this.mainWindow = mainWindow;
        this.userInterface = userInterface;
        
        //
        actualZoomArea.width = ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.width);
        actualZoomArea.height = ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.height);

        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, userInterface, dataLayer, (UserInterfaceLayeredPaneInnerInterface)this);

        // add panel to layered pane
        this.add(jPanelDraw, 1, 0);

        // create animation panel
        jPanelAnimation = new AnimationPanel(mainWindow, userInterface, dataLayer, jPanelDraw);

        // add panel to layered pane
        this.add(jPanelAnimation, 2, 0);

        
        // add this as observer to zoom manager
        ZoomManagerSingleton.getInstance().addObserver((Observer)this);
        // add as a language observer
        dataLayer.addLanguageObserver((Observer) this);
        // add as a preferences observer
        dataLayer.addPreferencesObserver((Observer) this);
 
        
        //
        setNewSizes();
    }

    @Override
    public void update(Observable o, Object o1) {
        /*
        switch ((ObserverUpdateEventType) o1) {
            case ZOOM_CHANGE:
            case GRAPH_SIZE_CHANGED:
                //Dimension dim = jPanelDraw.getPreferredSize();
                //jPanelAnimation.setPreferredSize(dim);
                //jPanelAnimation.setSize(dim);
                
                updateSizeAccordingToGraph();
                
                jPanelDraw.setPreferredSize(actualZoomArea);
                jPanelDraw.setSize(actualZoomArea);
                
                jPanelAnimation.setPreferredSize(actualZoomArea);
                jPanelAnimation.setSize(actualZoomArea);
                
                break;
        }*/
        
        switch ((ObserverUpdateEventType) o1) {
            case VIEW_DETAILS:
                // update images
                doUpdateImages();
                // udate size of this panel according to graph size, new labels could make graph bigger
                updateSizeAccordingToGraph();
                //
                break;
            case LANGUAGE:
                // update images
                doUpdateImages();
                //
                break;
            case GRAPH_COMPONENT_CHANGED:
                // update images
                doUpdateImages();
                // 
                break;
            case GRAPH_SIZE_CHANGED:
                // DO NOT CALL DO UPDATE IMAGES - IT WILL LOOP INDEFINETLY
                // udate size of this panel according to graph size
                updateSizeAccordingToGraph();
                // set new sizes
                setNewSizes();
                // 
                break;
            case ZOOM_CHANGE:
                doUpdateImages();
                //set new sizes of this (JDrawPanel) 
                setNewSizes();
                //
                break;
        }
        
        jPanelDraw.revalidate();
        jPanelDraw.repaint();
        
        jPanelAnimation.revalidate();
        jPanelAnimation.repaint();
        
        this.revalidate();
        this.repaint();
    }
    
    private void doUpdateImages() {
        // has to be here to perform before repaint
        if (jPanelDraw.hasGraph()) {
            jPanelDraw.getGraph().doUpdateImages();
        }
    }
    
    private void setNewSizes() {
        actualZoomArea.width = ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.width);
        actualZoomArea.height = ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.height);
        this.setSize(actualZoomArea);
        this.setPreferredSize(actualZoomArea);
        this.setMinimumSize(actualZoomArea);
        this.setMaximumSize(actualZoomArea);
        
        jPanelDraw.setSize(actualZoomArea);
        jPanelDraw.setPreferredSize(actualZoomArea);
        jPanelDraw.setMinimumSize(actualZoomArea);
        jPanelDraw.setMaximumSize(actualZoomArea);
        
        jPanelAnimation.setSize(actualZoomArea);
        jPanelAnimation.setPreferredSize(actualZoomArea);
        jPanelAnimation.setMinimumSize(actualZoomArea);
        jPanelAnimation.setMaximumSize(actualZoomArea);
    }

    private void updateSizeAccordingToGraph() {
        if (!jPanelDraw.hasGraph()) {
            actualZoomArea = new Dimension(ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomAreaMin));
            defaultZoomArea = new Dimension(defaultZoomAreaMin);
            return;
        }

        Dimension dim = jPanelDraw.getGraph().getPreferredSize();

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
        defaultZoomArea.setSize(ZoomManagerSingleton.getInstance().doScaleToDefault(actualZoomArea.width),
                ZoomManagerSingleton.getInstance().doScaleToDefault(actualZoomArea.height));
    }
    
    @Override
    public void doFitToGraphSize() {
        int graphWidthActual = jPanelDraw.getGraph().getWidth();
        int graphHeightActual = jPanelDraw.getGraph().getHeight();


        // validate if new size is smaller than defaultZoomAreaMin
        if (ZoomManagerSingleton.getInstance().doScaleToDefault(graphWidthActual) < defaultZoomAreaMin.getWidth()
                && ZoomManagerSingleton.getInstance().doScaleToDefault(graphHeightActual) < defaultZoomAreaMin.getHeight()) {
            // new size is smaller than defaultZoomAreaMin
            // set defaultZoomArea to defaultZoomAreaMin
            defaultZoomArea.setSize(defaultZoomAreaMin.width, defaultZoomAreaMin.height);

            // set area according to defaultZoomArea
            actualZoomArea.setSize(ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.width),
                    ZoomManagerSingleton.getInstance().doScaleToActual(defaultZoomArea.height));
        } else {
            // update area size
            actualZoomArea.setSize(graphWidthActual, graphHeightActual);
            // update default zoom size
            defaultZoomArea.setSize(ZoomManagerSingleton.getInstance().doScaleToDefault(actualZoomArea.width),
                    ZoomManagerSingleton.getInstance().doScaleToDefault(actualZoomArea.height));
        }

        // inform about size change using graph
        jPanelDraw.getGraph().doInformAboutSizeChange();
    }
    
/// from Draw panel outer interface
    @Override
    public boolean canUndo() {
        return jPanelDraw.canUndo();
    }

    @Override
    public boolean canRedo() {
        return jPanelDraw.canRedo();
    }

    @Override
    public void undo() {
        jPanelDraw.undo();
    }

    @Override
    public void redo() {
        jPanelDraw.redo();
    }

    @Override
    public AbstractAction getAbstractAction(DrawPanelAction action) {
        return jPanelDraw.getAbstractAction(action);
    }

    @Override
    public Graph removeGraph() {
        // remove this as observer from graph
        if (jPanelDraw.hasGraph()) {
            jPanelDraw.getGraph().deleteObserver(this);
        }

        jPanelAnimation.removeGraph();
        return jPanelDraw.removeGraph();
    }

    @Override
    public void setGraph(Graph graph) {
        jPanelDraw.setGraph(graph);
        jPanelAnimation.setGraph(graph);

        // observe for graph size changes
        graph.addObserver(this);
        
        // If opened graph before, and new graph is smaller, it will shrink the size of draw panel
        doFitToGraphSize();
    }

    @Override
    public boolean hasGraph() {
        return jPanelDraw.hasGraph();
    }

    @Override
    public Graph getGraph() {
        return jPanelDraw.getGraph();
    }

// IMPLEMENTS DrawPanelToolChangeOuterInterface    
    @Override
    public void removeCurrentMouseListener() {
        jPanelDraw.removeCurrentMouseListener();
    }

    @Override
    public DrawPanelListenerStrategy getMouseListener(MainTool tool) {
        return jPanelDraw.getMouseListener(tool);
    }

    @Override
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener) {
        jPanelDraw.setCurrentMouseListener(mouseListener);
    }

    @Override
    public void setCurrentMouseListenerSimulator() {
        jPanelDraw.setCurrentMouseListenerSimulator();
    }

    @Override
    public AnimationPanelOuterInterface getAnimationPanelOuterInterface() {
        return jPanelAnimation;
    }
}
