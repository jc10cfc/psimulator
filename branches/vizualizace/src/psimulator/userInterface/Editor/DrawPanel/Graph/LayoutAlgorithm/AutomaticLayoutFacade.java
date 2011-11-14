/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm;

import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class AutomaticLayoutFacade {
    
    public AutomaticLayoutFacade(){
    }
    
    public void automaticLayout(Graph g){
        GeneticAutomaticLayout genetic = new GeneticAutomaticLayout(g);
        
        //genetic.runGenetic(50, g.getHwComponents().size());
        
        genetic.runGenetic(20, g.getHwComponents().size()*2);
    }
    
}
