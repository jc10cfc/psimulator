

package psimulator.AbstractNetwork.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import psimulator.AbstractNetwork.AdditionsSimulator.SimNetworkDevice;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz> Lukáš <lukasma1@fit.cvut.cz>
 */
public class SimNetworkDeviceNullAdapter extends XmlAdapter<SimNetworkDevice,Integer> {

    @Override
    public Integer unmarshal(SimNetworkDevice v) throws Exception {
        return null;
    }

    @Override
    public SimNetworkDevice marshal(Integer v) throws Exception {
        return null;
    }


}
