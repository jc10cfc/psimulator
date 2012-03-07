

package shared.telnetConfig;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class ConfigRecord {

    /**
     * telnet port
     */
    private int port;
    /**
     * component ID
     */
    private int componentId;
    
    public ConfigRecord() {
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    
    
    
}
