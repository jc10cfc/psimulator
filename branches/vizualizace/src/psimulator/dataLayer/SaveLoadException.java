package psimulator.dataLayer;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class SaveLoadException extends Exception{
    public SaveLoadException(String msg){
      super(msg);
    }

    public SaveLoadException(String msg, Throwable t){
      super(msg,t);
    } 
}
