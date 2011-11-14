package psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Generation {
    
    private List<GeneticGraph> graphList;
    private GeneticGraph bestFitnessGraph;
    private double bestFitness;
    
    private final int maxScore = 100;
    
    public Generation(){
        graphList = new ArrayList<GeneticGraph>();
    }
    
    public void addGeneticGraph(GeneticGraph geneticGraph){
        graphList.add(geneticGraph);
    }

    public List<GeneticGraph> getGraphList() {
        return graphList;
    }

    public void evaluateFitness(){
        bestFitnessGraph = null;
        bestFitness = 0.0;
        
        for(GeneticGraph gg : graphList){
            gg.evaluateFitness();
            //System.out.println("fitness "+gg.getFitness());
            if(bestFitnessGraph == null || gg.getFitness() > bestFitness){
                bestFitness = gg.getFitness();
                bestFitnessGraph = gg;
            }
        }
        
        Collections.sort(graphList);
        
        int score = maxScore;
       
        for(GeneticGraph gg : graphList){
            gg.setScore(score);
            score = score -2;
        }

        
    }
    
    public double getBestFitness(){
        return bestFitness;
    }
    
    public GeneticGraph getBestFitnessGraph(){
        return bestFitnessGraph;
    }

    public int getMaxScore() {
        return maxScore;
    }
    
    
}
