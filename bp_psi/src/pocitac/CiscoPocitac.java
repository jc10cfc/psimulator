/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

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
}
