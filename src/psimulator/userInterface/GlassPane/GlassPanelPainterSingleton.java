package psimulator.userInterface.GlassPane;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class GlassPanelPainterSingleton {
    
    private MainWindowGlassPane mainWindowGlassPane;
    
    
    private GlassPanelPainterSingleton() {
    }
    
    public static GlassPanelPainterSingleton getInstance() {
        return GlassPanelPainterSingletonHolder.INSTANCE;
    }
    
    private static class GlassPanelPainterSingletonHolder {
        private static final GlassPanelPainterSingleton INSTANCE = new GlassPanelPainterSingleton();
    }
    
    public void initialize(MainWindowGlassPane mainWindowGlassPane){
        this.mainWindowGlassPane = mainWindowGlassPane;
    }
    
    
}
