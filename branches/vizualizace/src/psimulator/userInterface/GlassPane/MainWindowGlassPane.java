package psimulator.userInterface.GlassPane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MainWindowGlassPane extends JPanel implements TimingTarget, MainWindowGlassPaneMessageInterface{
    
    private Animator f_animator;
    //
    private List<MessageGraphic> messageList;
    
    private boolean animationInProgress  = false;
    private MessageGraphic currentMessage = null;
    
    public MainWindowGlassPane() {
        messageList = new ArrayList<>();
        
        // create animator
        f_animator = new Animator.Builder()
                .setDuration(10, TimeUnit.SECONDS)
                .setStartDirection(Animator.Direction.FORWARD)
                .addTarget((TimingTarget)this).build();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

        if(animationInProgress && currentMessage!=null){
            
            Graphics2D g2 = (Graphics2D) g;
        
            currentMessage.paintComponents(g);
            
            //String s = currentMessage.getTitle() + " | "+currentMessage.getMessageName() + " | " + currentMessage.getMessageValue();
            
            //g2.drawString(s, 30, 30);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }
    
    @Override
    public Point getLowerLeftCorner() {
        return new Point(0, this.getSize().height);
    }

    /**
     * Used by glas panel painter singleton to add messages
     * @param message 
     */
    public void addMessage(Message message){
        MessageGraphic messageGraphic = new MessageGraphic(message, (MainWindowGlassPaneMessageInterface)this);

        messageList.add(messageGraphic);
        
        if(!animationInProgress){
            startAnimation();
        }
    }
   
    /**
     * Call when you want to start animation
     */
    private void startAnimation(){
        // if nothing in list, return
        if(messageList.isEmpty()){
            return;
        }
        
        animationInProgress = true;

        currentMessage = messageList.get(0);
        messageList.remove(0);
        
        System.out.println("Start Animation");
        f_animator.start();
    }
    

    

    @Override
    public void begin(Animator source) {
        
    }

    @Override
    public void end(Animator source) {
        animationInProgress = false;
        
        System.out.println("Stop Animation");
        f_animator.stop();
        
        currentMessage = null;
        
        // start next
        startAnimation();
    }

    @Override
    public void repeat(Animator source) {
    }

    @Override
    public void reverse(Animator source) {
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
        // redisplay
        repaint();
    }
}
