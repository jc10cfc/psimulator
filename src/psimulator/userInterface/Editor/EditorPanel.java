package psimulator.userInterface.Editor;

import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;
import psimulator.userInterface.Editor.DrawPanel.ZoomEventWrapper;
import psimulator.userInterface.Editor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.Editor.DrawPanel.DrawPanel;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.Editor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;
import psimulator.userInterface.imageFactories.AwtImageFactory;

/**
 *
 * @author Martin
 */
public class EditorPanel extends EditorOuterInterface implements EditorInnerInterface, Observer{

    private EditorToolBar jToolBarEditor;
    //private DrawPanel jPanelDraw;
    private DrawPanelOuterInterface jPanelDraw;
    private JScrollPane jScrollPane;
    private AbstractImageFactory imageFactory;
    private MainWindowInnerInterface mainWindow;
    private DataLayerFacade dataLayer;

    public EditorPanel(MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer, AbstractImageFactory imageFactory) {
        super(new BorderLayout());
        
        this.mainWindow = mainWindow;
        this.dataLayer = dataLayer;
        
        this.imageFactory = imageFactory;

        // set border
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, (EditorInnerInterface) this, imageFactory, dataLayer);
        
        //create scroll pane
        jScrollPane = new JScrollPane(jPanelDraw);

        // add scroll bars
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // add scroll pane to panel
        this.add(jScrollPane, BorderLayout.CENTER);
        
        // create tool bar and add to panel
        jToolBarEditor = new EditorToolBar(dataLayer, imageFactory, jPanelDraw);
        
        // add tool bar to panel
        this.add(jToolBarEditor, BorderLayout.WEST);

        
        // set default tool in ToolBar
        doSetDefaultToolInToolBar();
        
        // add this to zoom Manager as Observer
        jPanelDraw.addObserverToZoomManager((Observer)this);
        
        // add listener for FitToSize button in tool bar
        jToolBarEditor.addToolActionFitToSizeListener(jPanelDraw.getAbstractAction(DrawPanelAction.FIT_TO_SIZE));
        
        // add listener for AlignToGrid button in tool bar
        jToolBarEditor.addToolActionAlignToGridListener(jPanelDraw.getAbstractAction(DrawPanelAction.ALIGN_COMPONENTS_TO_GRID));
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
        Point newViewPos = new Point();
        Rectangle oldView = jScrollPane.getViewport().getViewRect();
        
        
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



    

}
