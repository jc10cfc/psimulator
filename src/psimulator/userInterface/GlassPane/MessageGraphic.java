package psimulator.userInterface.GlassPane;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class MessageGraphic extends JPanel{
    
    //
    private Font titleFont;
    private Font nameFont;
    private Font valueFont;
    //
    private JLabel jLabelTitle;
    private JLabel jLabelName;
    private JLabel jLabelValue;
    
    public MessageGraphic(){
        titleFont = new Font("Tahoma", Font.BOLD, 10); // NOI18N
        nameFont = new Font("Tahoma", Font.PLAIN,  11);
        valueFont = new Font("Tahoma", Font.ITALIC,  11);

        initComponents();
    }
    
    public void setMessage(Message message){
        jLabelTitle.setText(message.getTitle());
        jLabelName.setText(message.getMessageName());
        jLabelValue.setText(message.getMessageValue());
    }
    
    private void initComponents() {
        // set background color to jpanel
        this.setBackground(new Color(255, 255, 204));
        
        jLabelTitle = new JLabel();       
        jLabelTitle.setFont(titleFont); // NOI18N
        jLabelTitle.setFocusable(false);
        
        jLabelName = new JLabel();
        jLabelName.setFont(nameFont); // NOI18N
        jLabelName.setFocusable(false);
        
        jLabelValue = new JLabel();
        jLabelValue.setFont(valueFont); // NOI18N
        jLabelValue.setFocusable(false);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTitle)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelName)
                    .addComponent(jLabelValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
}
