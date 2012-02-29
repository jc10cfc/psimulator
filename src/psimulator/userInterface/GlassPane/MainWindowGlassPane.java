package psimulator.userInterface.GlassPane;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindowGlassPane extends JPanel {
    
    private List<Message> messageList;
    
    private boolean animationInProgress;
    
    public MainWindowGlassPane() {
        messageList = new ArrayList<>();
    }
    
    public void addMessage(Message message){
        messageList.add(message);
        
        if(!animationInProgress){
            startAnimation();
        }
    }
    
    public void removeMessage(Message message){
        messageList.remove(message);
    }
  
    private void startAnimation(){
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
}
