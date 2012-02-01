package psimulator.userInterface.SimulatorEditor.DrawPanel.Support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Martin
 */
public class Validator {
    
    public static final String IP_WITH_MASK_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])/"
                + "([0-9]|[1-2][0-9]|3[0-2])$";
    
    public static boolean validateIpAddress(String address) {

        // check the IP address
        Pattern pattern = Pattern.compile(IP_WITH_MASK_PATTERN);
        Matcher matcher = pattern.matcher(address);

        // if not valid
        if (!matcher.matches()) {
            return false;
        }

        // everything is ok
        return true;
    }   
}
