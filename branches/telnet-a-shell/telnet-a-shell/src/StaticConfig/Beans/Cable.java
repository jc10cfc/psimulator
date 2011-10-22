/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

/**
 *
 * @author zaltair
 */
class Cable {

    Property<String> name = new Property<String>(Util.getText("Cable.name.name"), Util.getText("Cable.name.description"));
    Property<Integer> slowDown = new Property<Integer>(Util.getText("Cable.slowDown.name"), Util.getText("Cable.slowDown.description"));

    /**
     * cable is connected to the interface in computer
     */
    NetworkInterface networkInterface1;
    NetworkInterface networkInterface2;


}
