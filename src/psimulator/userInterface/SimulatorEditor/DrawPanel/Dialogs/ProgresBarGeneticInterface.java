
package psimulator.userInterface.SimulatorEditor.DrawPanel.Dialogs;

import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.LayoutAlgorithm.GeneticGraph;

/**
 *
 * @author Martin
 */
public interface ProgresBarGeneticInterface {
    
    public void informProgress(int generation, double fitness);
    
    public void informSuccessEnd(int generation, double fitness, GeneticGraph geneticGraph);
    
    
}
