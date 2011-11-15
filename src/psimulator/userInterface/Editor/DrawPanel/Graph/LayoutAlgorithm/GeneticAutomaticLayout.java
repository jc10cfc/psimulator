package psimulator.userInterface.Editor.DrawPanel.Graph.LayoutAlgorithm;

import java.awt.Dimension;
import java.util.Random;
import javax.swing.JFrame;
import psimulator.userInterface.Editor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class GeneticAutomaticLayout {

    int populationSize;
    int gridSize;
    int generationCount;
    private Graph graph;
    private GeneticGraph bestGeneticGraph;
    
    private GeneticGraph elitisticGraph;
    
    private JFrame visualizeFrame;
    private VisualizePanel visualizePanel;
    private Random random = new Random();
    
    private double lastFitnessRemembered = Integer.MIN_VALUE;
    private int lastFitnessRememberedGeneration = 0;

    public GeneticAutomaticLayout(Graph graph) {
        this.graph = graph;

        this.visualizeFrame = new JFrame("Vizualizace prubehu algoritmu");
        this.visualizePanel = new VisualizePanel();
        this.visualizeFrame.add(visualizePanel);

        visualizeFrame.setSize(new Dimension(800, 600));
    }

    public void runGenetic(int populationSize, int gridSize) {
        this.populationSize = populationSize;
        this.gridSize = gridSize;

        generationCount = 0;

        GeneticGraph initialGeneticGraph = new GeneticGraph(graph, gridSize);

        Generation oldGeneration = initPopulation(initialGeneticGraph);
        oldGeneration.evaluateFitness();

        //System.out.println("Best fitness = "+generation.getBestFitness()+", graph:");
        //System.out.println(generation.getBestFitnessGraph());

        visualizeFrame.setVisible(true);

        visualize(oldGeneration.getBestFitnessGraph());
        
        while (!termConditionSatisfied(oldGeneration)) {
            generationCount++;
            /*
            System.out.println("Before selection");
            for(GeneticGraph gg : oldGeneration.getGraphList()){
                System.out.println("Graph fitness="+gg.getFitness()+", score= "+ gg.getScore());
            }*/
            
            // DO SELECTION
            Generation newGeneration = doSelection(oldGeneration);
            
            /*System.out.println("After selection");
            for(GeneticGraph gg : newGeneration.getGraphList()){
                System.out.println("Graph fitness="+gg.getFitness()+", score= "+ gg.getScore());
            }*/
            
            // DO CROSSOVER
            newGeneration = doCrossover2(newGeneration);

            // DO MUTATE
            doMutate(newGeneration);

            newGeneration.addGeneticGraph(elitisticGraph);
            
            // EVALUATE FITNESS
            newGeneration.evaluateFitness();

            System.out.println("Best fitness in genereation " + generationCount + " is " + newGeneration.getBestFitness());
            System.out.println("Generation size = " + newGeneration.getGraphList().size());

            oldGeneration = newGeneration;
        }


        //visualize(oldGeneration.getBestFitnessGraph());
        visualize(bestGeneticGraph);
        System.out.println("best genetic graph from generation: "+lastFitnessRememberedGeneration+", fitness = "+ lastFitnessRemembered+
                "fitness int:"+(int)lastFitnessRemembered);
        
        /*
        while(!termConditionSatisfied(generation)){
        generationCount ++;
        
        doSelection();
        
        doCrossover();
        
        doMutate();
        
        generation.evaluateFitness();
        }*/

    }

    private void visualize(GeneticGraph graph) {
        visualizePanel.setGraph(graph);
        visualizePanel.repaint();
        visualizeFrame.revalidate();
    }
    
    private Generation doCrossover2(Generation oldGeneration){
        Generation generation = new Generation();
        
        int listSize = oldGeneration.getGraphList().size();
      
        for(int i = 0; i < listSize-1; i= i+2){
            GeneticGraph[] tmp = oldGeneration.getGraphList().get(i).crossoverRandomSinglePoint(oldGeneration.getGraphList().get(i+1));
            generation.addGeneticGraph(tmp[0]);
            generation.addGeneticGraph(tmp[1]);
             
        }
        return generation;
    }

    private void doMutate(Generation newGeneration) {

        for (GeneticGraph gg : newGeneration.getGraphList()) {
            double rouletteRandom = random.nextDouble();
            if (rouletteRandom <= 0.1) { // 40% probabbility
               randomMutateExecute(gg); 
            }
        }
    }
 
    private void randomMutateExecute(GeneticGraph gg){
        int number = random.nextInt(5);
        if(number == 0){
            gg.singleNodeMutate();
        }else if(number == 1){
            gg.singleEdgeMutate1();
        }else if(number == 3){
            gg.nodeWithMostNeighboursMutate();
        }else if(number == 4){
            gg.singleEdgeMutate2();
        }
        
        //gg.nodeWithMostNeighboursMutate();
    }

    
    /**
     * roulette wheel selection
     * @param oldGeneration
     * @return 
     */
    private Generation doSelection(Generation oldGeneration){
        
        Generation newGeneration = new Generation();
        
        int sum=0;
        
        for(int i=0;i<oldGeneration.getGraphList().size();i++){
            sum+=oldGeneration.getGraphList().get(i).getScore();
        }
        
        // elitisizm - preserve the best sollution
        elitisticGraph = oldGeneration.getGraphList().get(oldGeneration.getGraphList().size()-1).clone();
        
        while(newGeneration.getGraphList().size() < populationSize){
            int counter = 0;
            int randomNumber = random.nextInt(sum)+1;
            
            for(int i=0;i<oldGeneration.getGraphList().size();i++){
                counter += oldGeneration.getGraphList().get(i).getScore();
                if(randomNumber < counter){
                    GeneticGraph tmp = oldGeneration.getGraphList().get(i).clone();
                    newGeneration.addGeneticGraph(tmp);
                    break;
                }
            }
            
            
        }
        
        
        return newGeneration;
    }
    
    

    private boolean termConditionSatisfied(Generation generation) {
        double bestFitness = generation.getBestFitness();
 
        if(bestFitness > lastFitnessRemembered){
            lastFitnessRemembered = bestFitness;
            lastFitnessRememberedGeneration = generationCount;
            bestGeneticGraph = generation.getBestFitnessGraph().clone();
        }
        
        
        if(lastFitnessRememberedGeneration < generationCount - 3000 ){
            return true;
        }
        /*
        if (generationCount > 2000) {
            return true;
        }*/
        return false;
    }

    private Generation initPopulation(GeneticGraph initialGeneticGraph) {
        Generation generation = new Generation();

        // add first geneticGraph
        generation.addGeneticGraph(initialGeneticGraph);

        // clone 49 graphs
        for (int i = 0; i < populationSize - 1; i++) {
            // clone
            GeneticGraph tmp = initialGeneticGraph.clone();
            // random init
            tmp.placeNodesRandomly();

            // add to generation
            generation.addGeneticGraph(tmp);
            //System.out.println("tmp:");
            //System.out.println(tmp.toString());
        }

        return generation;
    }
}
