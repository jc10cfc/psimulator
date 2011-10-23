package psimulator.userInterface.Editor;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInterface;
import psimulator.userInterface.imageFactories.AbstractImageFactory;
import psimulator.userInterface.imageFactories.AwtImageFactory;

/**
 *
 * @author Martin
 */
public class EditorPanel extends AbstractEditor implements Observer{

    private EditorToolBar jToolBarEditor;
    private DrawPanel jPanelDraw;
    private JScrollPane jScrollPane;
    private AbstractImageFactory imageFactory;
    private MainWindowInterface mainWindow;
    private DataLayerFacade dataLayer;

    public EditorPanel(MainWindowInterface mainWindow, DataLayerFacade dataLayer) {
        super(new BorderLayout());
        
        this.mainWindow = mainWindow;
        this.dataLayer = dataLayer;
        
        imageFactory = new AwtImageFactory();

        // set border
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, imageFactory, dataLayer);
        
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

        jToolBarEditor.addToolActionFitToSizeListener(new JMenuToolFitToSizeActionListener());
    }
    
    @Override
    public void init(){
        jPanelDraw.getZoomManager().addObserver(this);
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
   
    /////////////////////-----------------------------------////////////////////
    /**
     * Action Listener for FitToSize button
     */
    class JMenuToolFitToSizeActionListener implements ActionListener {

        /**
         * calls zoom operation on jPanelEditor according to actionCommand
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // update jPanelDraw size
            jPanelDraw.updateSizeToFitComponents();
        }
    }

    @Override
    public boolean canUndo() {
        return jPanelDraw.getUndoManager().canUndo();
    }

    @Override
    public boolean canRedo() {
        return jPanelDraw.getUndoManager().canRedo();
    }

    @Override
    public void undo() {
        jPanelDraw.getUndoManager().undo();
    }

    @Override
    public void redo() {
        jPanelDraw.getUndoManager().redo();
    }

    @Override
    public boolean canZoomIn() {
        return jPanelDraw.getZoomManager().canZoomIn();
    }

    @Override
    public boolean canZoomOut() {
        return jPanelDraw.getZoomManager().canZoomOut();
    }

    @Override
    public void zoomIn() {
        // TODO: Point of zoom in parameter
        
        jPanelDraw.getZoomManager().zoomIn();
    }

    @Override
    public void zoomOut() {
        // TODO: Point of zoom in parameter
        
        jPanelDraw.getZoomManager().zoomOut();
    }

    @Override
    public void zoomReset() {
        // TODO: Point of zoom in parameter
        
        jPanelDraw.getZoomManager().zoomReset();
    }

}
