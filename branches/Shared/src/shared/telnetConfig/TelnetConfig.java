

package shared.telnetConfig;

import java.util.Map;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class TelnetConfig {
    
    /**
     * key is a componentID
     */
    Map<Integer,ConfigRecord> configRecords;

    public TelnetConfig() {
    }

    public Map<Integer, ConfigRecord> getConfigRecords() {
        return configRecords;
    }

    public void setConfigRecords(Map<Integer, ConfigRecord> configRecords) {
        this.configRecords = configRecords;
    }

    /**
     * 
     * @param key compnent ID
     * @param value
     * @return 
     */
    public ConfigRecord put(Integer key, ConfigRecord value) {
        return configRecords.put(key, value);
    }
    
    
    

}
