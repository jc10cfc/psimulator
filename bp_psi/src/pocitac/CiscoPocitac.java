/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import datoveStruktury.*;
import datoveStruktury.WrapperRoutovaciTabulkyCisco;

/**
 *
 * @author haldyr
 */
public class CiscoPocitac extends AbstractPocitac{

    private WrapperRoutovaciTabulkyCisco wrapper;

    public CiscoPocitac(String jmeno, int port) {
        super(jmeno,port);
        wrapper = new WrapperRoutovaciTabulkyCisco(this);
    }

    public WrapperRoutovaciTabulkyCisco getWrapper() {
        return wrapper;
    }

    @Override
    public boolean prijmiEthernetove(Paket p, SitoveRozhrani rozhr, IpAdresa ocekavana) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
