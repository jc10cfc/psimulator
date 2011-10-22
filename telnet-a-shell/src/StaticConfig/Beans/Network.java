/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

import java.util.List;

/**
 *
 * @author zaltair
 */
public class Network {

     Property<String> networkName = new Property<String>(Util.getText("Network.networkName.Name"),Util.getText("Network.networkName.Description"));
     List<Computer> computers;
     List<Cable> cables;

    public List<Computer> getComputers() {
        return computers;
    }

    public void setComputers(List<Computer> computers) {
        this.computers = computers;
    }

    public Property<String> getNetworkName() {
        return networkName;
    }

    public void setNetworkName(Property<String> networkName) {
        this.networkName = networkName;
    }

   


    


}
