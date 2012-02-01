package psimulator.userInterface.SimulatorEditor.DrawPanel.Support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Martin
 */
public class Validator {
    
    public static boolean validateIpAddress(String address) {

        String[] tmp = address.split("/");

        if (tmp.length != 2) {
            return false;
        }

        String ip = tmp[0];
        String mask = tmp[1];

        //System.out.println("IP:" + ip);
        //System.out.println("MASK:" + mask);

        // check the IP address
        String PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);

        // if IP not valid
        if (!matcher.matches()) {
            return false;
        }
        
        // parse mask
        int value;
        
        try{
            value = Integer.parseInt(mask);
        }catch(NumberFormatException ex){
            return false;
        }
        
        // check mask
        if(value > 32 || value < 00){
            return false;
        }

        // everything is ok
        return true;
    }   
}
