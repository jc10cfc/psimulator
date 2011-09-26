/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.util.ArrayList;

/**
 *
 * @author zaltair
 */
public class History {

    private ArrayList<String> history = new ArrayList<String>();
    private int historyIterator = 0;
    
    public void add(String command){
        this.history.add(command);
    }

     public String getPreviousCommand() {

        if (history.isEmpty()) {
            return "";
        }

        if (historyIterator < history.size()) {
            historyIterator++;
        }

        return history.get(history.size() - historyIterator);

    }

    public String getNextCommand() {

        if (history.isEmpty()) {
            return "";
        }

        if (historyIterator > 1) {
            historyIterator--;
        } else if (historyIterator <= 1) {
            historyIterator = 0;
            return "";
        }

        return history.get(history.size() - historyIterator);

    }

}
