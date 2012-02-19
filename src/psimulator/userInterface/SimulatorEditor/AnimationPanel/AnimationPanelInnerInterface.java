package psimulator.userInterface.SimulatorEditor.AnimationPanel;

import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.PacketImageType;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface AnimationPanelInnerInterface {
    
    public void removeAnimation(Animation animation);
    
    public PacketImageType getPacketImageType();
}
