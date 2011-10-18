package psimulator.userInterface.Editor.Components;

/**
 *
 * @author Martin
 */
public class EthInterface {
    private String name;
    private Cable cable;

    public EthInterface(String name, Cable cable){
        this.name = name;
        this.cable = cable;
    }

    public boolean hasCable(){
        if(cable == null){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean hasCable(Cable c){
        return c == cable;
    }
    
    public Cable getCable() {
        return cable;
    }

    public void setCable(Cable cable) {
        this.cable = cable;
    }
    
    public void removeCable(){
        this.cable = null;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
