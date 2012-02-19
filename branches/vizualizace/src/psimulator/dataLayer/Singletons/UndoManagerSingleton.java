package psimulator.dataLayer.Singletons;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class UndoManagerSingleton {
    
    private UndoManagerSingleton() {
    }
    
    public static UndoManagerSingleton getInstance() {
        return UndoManagerSingletonHolder.INSTANCE;
    }
    
    private static class UndoManagerSingletonHolder {

        private static final UndoManagerSingleton INSTANCE = new UndoManagerSingleton();
    }
}
