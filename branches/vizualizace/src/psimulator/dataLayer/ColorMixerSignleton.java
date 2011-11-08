package psimulator.dataLayer;

import java.awt.Color;

/**
 *
 * @author Martin
 */
public class ColorMixerSignleton {

    //public static Color mainToolbarColor = new Color(198, 83, 83);
    public static Color mainToolbarColor = new Color(164, 194, 245);
    //public static Color editToolbarColor = new Color(213, 129, 129);
    public static Color editToolbarColor = Color.LIGHT_GRAY;
    public static Color drawPanelColor = Color.WHITE;
    
    private static ColorMixerSignleton colorMixerSignletonObject;

    /** A private Constructor prevents any other class from instantiating. */
    private ColorMixerSignleton () {
        //	 Optional Code
    }

    public static synchronized ColorMixerSignleton getSingletonObject() {
        if (colorMixerSignletonObject == null) {
            colorMixerSignletonObject = new ColorMixerSignleton();
        }
        return colorMixerSignletonObject;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
