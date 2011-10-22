/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

import java.util.ResourceBundle;

/**
 *
 * @author zaltair
 */
public class Util {

    public static String getText(String text){

        return ResourceBundle.getBundle("StaticConfig.Beans.Text").getString(text);
    }


}
