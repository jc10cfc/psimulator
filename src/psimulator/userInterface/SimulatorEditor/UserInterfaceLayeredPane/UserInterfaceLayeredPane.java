package psimulator.userInterface.SimulatorEditor.UserInterfaceLayeredPane;

import java.awt.Dimension;
import java.util.Observer;
import javax.swing.AbstractAction;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanel;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;

/**
 *
 * @author Martin
 */
public class UserInterfaceLayeredPane extends UserInterfaceLayeredPaneOuterInterface{

    private DrawPanelOuterInterface jPanelDraw; // draw panel
    private AnimationPanelOuterInterface jPanelAnimation; // animation panel
    //
    //private ZoomManager zoomManager = new ZoomManager();
    //
    private AbstractImageFactory imageFactory;
    private MainWindowInnerInterface mainWindow;
    private UserInterfaceMainPanelInnerInterface userInterface;
    

    public UserInterfaceLayeredPane(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface userInterface,
            AbstractImageFactory imageFactory, DataLayerFacade dataLayer) {

        //
        this.mainWindow = mainWindow;
        this.imageFactory = imageFactory;
        this.userInterface = userInterface;
        
        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, userInterface, imageFactory, dataLayer);
        
        // add panel to layered pane
        this.add(jPanelDraw, 1, 0);
        
        // create animation panel
        jPanelAnimation = new AnimationPanel(mainWindow, userInterface, imageFactory, dataLayer, jPanelDraw);
        
        // add panel to layered pane
        this.add(jPanelAnimation, 2, 0);
        
        // add jPanelAnimation as observer to zoom manager
        ZoomManagerSingleton.getInstance().addObserver(jPanelAnimation);
        
        // add jPanelAnimation as observer to preferences manager
        dataLayer.addPreferencesObserver(jPanelAnimation);
    }

    @Override
    public Dimension getPreferredSize() {
        //System.out.println("GetPrefSize");
        return jPanelDraw.getPreferredSize();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        //System.out.println("SetSize2");
        jPanelDraw.setSize(width, height);
        jPanelAnimation.setSize(width, height);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        //System.out.println("SetSize1");
        jPanelDraw.setSize(d);
        jPanelAnimation.setSize(d);
    }

    @Override
    public void updateSize() {
        Dimension d = jPanelDraw.getGraph().getPreferredSize();
        setSize(d);
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
        jPanelAnimation.removeGraph();
        return jPanelDraw.removeGraph();
    }

    @Override
    public void setGraph(Graph graph) {
        jPanelDraw.setGraph(graph);
        jPanelAnimation.setGraph(graph);
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
    public AnimationPanelOuterInterface getAnimationPanelOuterInterface(){
        return jPanelAnimation;
    }

    

    
}
