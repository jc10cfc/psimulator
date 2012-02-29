package psimulator.userInterface.GlassPane;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import psimulator.dataLayer.Singletons.ImageFactory.ImageFactorySingleton;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MessageGraphic extends JComponent{
    
    private MainWindowGlassPaneMessageInterface glassPanel;
    //
    private Font titleFont;
    private Font nameFont;
    private Font valueFont;
    //
    private BufferedImage titleImage;
    private BufferedImage nameImage;
    private BufferedImage valueImage;
    
    public MessageGraphic(Message message, MainWindowGlassPaneMessageInterface glassPanel){
        this.glassPanel = glassPanel;
        
        titleFont = new Font("Courier", Font.BOLD,  8);
        nameFont = new Font("Courier", Font.PLAIN,  10);
        valueFont = new Font("Courier", Font.ITALIC,  10);
        
        titleImage = getImageForText(message.getTitle(), titleFont);
        nameImage = getImageForText(message.getMessageName(), nameFont);
        valueImage = getImageForText(message.getMessageValue(), valueFont);
        
        Dimension d = new Dimension(titleImage.getWidth(), titleImage.getHeight());
        
        this.setSize(d);
        this.setPreferredSize(d);
    }
    
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//

        Graphics2D g2 = (Graphics2D) g;
        
        Point p = glassPanel.getLowerLeftCorner();
        
        g2.drawImage(titleImage, p.x, p.y - titleImage.getHeight(), null);
    }
    
    
    private BufferedImage getImageForText(String text, Font font){
        // get font metrics
        FontMetrics fm = ImageFactorySingleton.getInstance().getFontMetrics(font);
        
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent() + fm.getDescent();

        return ImageFactorySingleton.getInstance().getImageWithText(text, font, textWidth, textHeight, fm.getMaxAscent());
    }
    
}
