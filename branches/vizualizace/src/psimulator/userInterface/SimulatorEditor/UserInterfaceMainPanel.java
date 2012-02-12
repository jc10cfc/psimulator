package psimulator.userInterface.SimulatorEditor;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomEventWrapper;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class UserInterfaceMainPanel extends UserInterfaceMainPanelOuterInterface implements UserInterfaceMainPanelInnerInterface, 
        Observer{

    private AbstractImageFactory imageFactory;
    private MainWindowInnerInterface mainWindow;
    private DataLayerFacade dataLayer;
    //
    //
    private UserInterfaceMainPanelState userInterfaceState;
    //
    private EditorToolBar jToolBarEditor;   // certical tool bar with hand, computer, switches
    //
    private DrawPanelOuterInterface jPanelDraw; // draw panel
    private JScrollPane jScrollPane;            // scroll pane with draw panel
    //
    private SimulatorControlPanel jPanelSimulator;
    //
    private WelcomePanel jPanelWelcome;
   

    public UserInterfaceMainPanel(MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer, AbstractImageFactory imageFactory,
            UserInterfaceMainPanelState userInterfaceState) {
        super(new BorderLayout());
        
        this.mainWindow = mainWindow;
        this.dataLayer = dataLayer;
        this.imageFactory = imageFactory;
        
        // set border
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        
        // ----------- DRAW PANEL CREATION -----------------------
        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, (UserInterfaceMainPanelInnerInterface) this, imageFactory, dataLayer);
        
        //create scroll pane
        jScrollPane = new JScrollPane(jPanelDraw);

        // add scroll bars
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        
        // ----------- EDITOR STUFF CREATION -----------------------
        // create tool bar
        jToolBarEditor = new EditorToolBar(dataLayer, imageFactory, jPanelDraw);
        
        // add listener for FitToSize button in tool bar
        jToolBarEditor.addToolActionFitToSizeListener(jPanelDraw.getAbstractAction(DrawPanelAction.FIT_TO_SIZE));
        
        // add listener for AlignToGrid button in tool bar
        jToolBarEditor.addToolActionAlignToGridListener(jPanelDraw.getAbstractAction(DrawPanelAction.ALIGN_COMPONENTS_TO_GRID));
        
        
        // ----------- SIMULATOR STUFF CREATION -----------------------
        // create simulator panel
        jPanelSimulator = new SimulatorControlPanel(dataLayer);
        
        // add as a language observer
        dataLayer.addLanguageObserver((Observer)jPanelSimulator);
        
        // add as a simulator observer
        dataLayer.addSimulatorObserver((Observer)jPanelSimulator);
        
        
        // ----------- WELCOME STUFF CREATION -----------------------
        // create welcome panel
        jPanelWelcome = new WelcomePanel(dataLayer);
        
        // add as a language observer
        dataLayer.addLanguageObserver((Observer)jPanelWelcome);
        
        
        // ----------- rest of constructor -----------------------
        // add this to zoom Manager as Observer
        jPanelDraw.addObserverToZoomManager((Observer)this);
        
        
        doChangeMode(userInterfaceState);
    }
    
    @Override
    public final void doChangeMode(UserInterfaceMainPanelState userInterfaceState) {
        this.userInterfaceState = userInterfaceState;
        
        this.removeAll();
        
       // turn of activities in simulator
        jPanelSimulator.setTurnedOff();
        
        switch(userInterfaceState){
            case WELCOME:
                this.add(jPanelWelcome, BorderLayout.CENTER);
                break;
            case EDITOR:
                this.add(jScrollPane, BorderLayout.CENTER);
                this.add(jToolBarEditor, BorderLayout.WEST);
                
                // set default tool in ToolBar
                jPanelDraw.removeCurrentMouseListener();
                doSetDefaultToolInToolBar();
                break;
            case SIMULATOR:
                this.add(jScrollPane, BorderLayout.CENTER);
                this.add(jPanelSimulator, BorderLayout.EAST);
                
                // set SIMULATOR mouse listener in draw panel
                jPanelDraw.removeCurrentMouseListener();
                jPanelDraw.setCurrentMouseListenerSimulator();
                break;
        }
        
        // repaint
        this.revalidate();
        this.repaint();
    }

    /**
     * reaction to zoom event
     * @param o
     * @param o1 
     */
    @Override
    public void update(Observable o, Object o1) {
        ZoomEventWrapper zoomEventWrapper = ((ZoomManager)o).getZoomEventWrapper();     
        
        // -------------- ZOOM ACCORDING TO MOUSE POSITION  ---------------------
        Point oldPosition = jScrollPane.getViewport().getViewPosition();
        
        // get old mouse position
        int mouseXOldZoom = zoomEventWrapper.getMouseXInOldZoom();
        int mouseYOldZoom = zoomEventWrapper.getMouseYInOldZoom();
        
        // count distance of old mouse from old viewport
        int width = mouseXOldZoom - oldPosition.x;
        int height = mouseYOldZoom - oldPosition.y;
        
        // count new mouse coordinates
        int mouseXNewZoom = (int)((mouseXOldZoom / zoomEventWrapper.getOldScale()) * zoomEventWrapper.getNewScale());
        int mouseYNewZoom = (int)((mouseYOldZoom / zoomEventWrapper.getOldScale()) * zoomEventWrapper.getNewScale());
        
        
        Point newPosition = new Point();
        // new viewport position has to be in same distance from mouse as before
        newPosition.x = mouseXNewZoom - width;
        newPosition.y = mouseYNewZoom - height;
        
        // do not allow position below 0,0
        if(newPosition.x < 0){
            newPosition.x = 0;
        }
        
        if(newPosition.y < 0){
            newPosition.y = 0;
        }
        
        /*
        System.out.println("Old viewport x="+oldPosition.x+", y="+oldPosition.y);
        System.out.println("Old mouse x = "+mouseXOldZoom+", y= "+mouseYOldZoom+". New zoom x="+mouseXNewZoom+", y="+mouseYNewZoom);
        
        System.out.println("New viewport x="+newPosition.x+", y="+newPosition.y);
        */
        
        // set new viewport
        jScrollPane.getViewport().setViewPosition(newPosition);
        
        // END -------------- ZOOM ACCORDING TO MOUSE POSITION  ---------------------
        
        // update zoom buttons in main window
        mainWindow.updateZoomButtons();
        // repaint
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void init() {
        jPanelSimulator.clearEvents();
    }

    @Override
    public Graph removeGraph() {
        return jPanelDraw.removeGraph();
    }

    @Override
    public void setGraph(Graph graph) {
        jPanelDraw.setGraph(graph);
    }
    
    @Override
    public Graph getGraph() {
        return jPanelDraw.getGraph();
    }
    
    @Override
    public boolean hasGraph() {
        return jPanelDraw.hasGraph();
    }
    
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
    public boolean canZoomIn() {
        return jPanelDraw.canZoomIn();
    }

    @Override
    public boolean canZoomOut() {
        return jPanelDraw.canZoomOut();
    }

    @Override
    public void zoomIn() {
        // TODO: Point of zoom in parameter
        jPanelDraw.zoomIn();
    }

    @Override
    public void zoomOut() {
        // TODO: Point of zoom in parameter
        jPanelDraw.zoomOut();
    }

    @Override
    public void zoomReset() {
        // TODO: Point of zoom in parameter
        jPanelDraw.zoomReset();
    }

    @Override
    public final void doSetDefaultToolInToolBar() {
        // set default tool in ToolBar
        jToolBarEditor.setDefaultTool();
    }
    
    @Override
    public UserInterfaceMainPanelState getUserInterfaceState() {
        return userInterfaceState;
    }

    @Override
    public void addNewProjectActionListener(ActionListener listener) {
        jPanelWelcome.addNewProjectActionListener(listener);
    }

    @Override
    public void addOpenProjectActionListener(ActionListener listener) {
        jPanelWelcome.addOpenProjectActionListener(listener);
    }

    

    

    



    

}
