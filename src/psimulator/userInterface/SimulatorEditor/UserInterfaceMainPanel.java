package psimulator.userInterface.SimulatorEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomEventWrapper;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class UserInterfaceMainPanel extends UserInterfaceMainPanelOuterInterface implements UserInterfaceMainPanelInnerInterface, Observer{

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
    private JPanel jPanelSimulator;
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
        
        switch(userInterfaceState){
            case WELCOME:
                this.add(jPanelWelcome, BorderLayout.CENTER);
                break;
            case EDITOR:
                this.add(jScrollPane, BorderLayout.CENTER);
                this.add(jToolBarEditor, BorderLayout.WEST);
                
                // set default tool in ToolBar
                doSetDefaultToolInToolBar();
                break;
            case SIMULATOR:
                this.add(jScrollPane, BorderLayout.CENTER);
                this.add(jPanelSimulator, BorderLayout.EAST);
                
                // TODO: set some tool for simulator
                
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
        ZoomEventWrapper zoomWrapper = (ZoomEventWrapper) o1;
        // set viewport
        /*
        Point newViewPos = new Point();
        Rectangle oldView = jScrollPane.getViewport().getViewRect();
        
        System.out.println("\nZoom changed");
        System.out.println("Mouse pos x: "+zoomWrapper.getMouseX()+", y:"+zoomWrapper.getMouseY());
        
        System.out.println("Old view: "+oldView);
        System.out.println("Draw panel width: "+jPanelDraw.getWidth());
        
        newViewPos.x = (jPanelDraw.getWidth() - jScrollPane.getWidth()) /2;
        newViewPos.y = (jPanelDraw.getHeight() - jScrollPane.getHeight()) /2;
        */
        //jScrollPane.getViewport().setViewPosition(newViewPos);
        
        
        // update zoom buttons in main window
        mainWindow.updateZoomButtons();
        // repaint
        this.revalidate();
        this.repaint();
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
