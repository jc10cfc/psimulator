/*
 * Erstellt am 27.10.2011.
 * TODO: implementovat
 */

package networkModule;

import dataStructures.L2Packet;
import device.AbstractDevice;
import physicalModule.AbstractInterface;

/**
 * Implementation of network module of generic simple switch.
 * @author neiss
 */
public class SimpleSwitchNetMod extends NetMod  {

    public SimpleSwitchNetMod(AbstractDevice device) {
        super(device);
    }

    @Override
    public void receivePacket(L2Packet packet, AbstractInterface iface) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
