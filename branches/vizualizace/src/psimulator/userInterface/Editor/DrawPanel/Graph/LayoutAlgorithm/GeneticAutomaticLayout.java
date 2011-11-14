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

            // DO SELECTION
            Generation newGeneration = doSelection(oldGeneration);

            // DO CROSSOVER
            //doCrossover(newGeneration);

            // DO MUTATE
            doMutate(newGeneration);

            // EVALUATE FITNESS
            newGeneration.evaluateFitness();

            System.out.println("Best fitness in genereation " + generationCount + " is " + newGeneration.getBestFitness());
            System.out.println("Generation size = " + newGeneration.getGraphList().size());

            oldGeneration = newGeneration;
        }


        visualize(oldGeneration.getBestFitnessGraph());

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

    
    private void doCrossover(Generation oldGeneration) {
        Generation newGeneration = new Generation();
        for (GeneticGraph gg : oldGeneration.getGraphList()) {
            double probability = gg.getScore() / 150.0;
            double rouletteRandom = random.nextDouble();

            if (rouletteRandom <= probability) {
                if (oldGeneration.getGraphList().size() + newGeneration.getGraphList().size() <= populationSize) {
                    newGeneration.addGeneticGraph(gg.clone());
                }
            }
        }

        newGeneration.getGraphList().addAll(oldGeneration.getGraphList());
        oldGeneration = newGeneration;
    }

    private void doMutate(Generation newGeneration) {

        GeneticGraph best = newGeneration.getGraphList().get(0).clone();
        
        for (GeneticGraph gg : newGeneration.getGraphList()) {
            double rouletteRandom = random.nextDouble();
            if (rouletteRandom <= 40) { // 40% probabbility
               randomMutateExecute(gg); 
            }
        }

        for (int i = newGeneration.getGraphList().size(); i < populationSize; i++) {
            GeneticGraph tmp = newGeneration.getGraphList().get(0).clone();
            tmp.placeNodesRandomly();
            newGeneration.addGeneticGraph(tmp);
        }
        
        newGeneration.addGeneticGraph(best);
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


    private Generation doSelection(Generation oldGeneration) {
        Generation newGeneration = new Generation();
        for (GeneticGraph gg : oldGeneration.getGraphList()) {
            double probability = gg.getScore() / 100.0;
            double rouletteRandom = random.nextDouble();

            if (rouletteRandom <= probability) {
                newGeneration.addGeneticGraph(gg);
            }

        }

        return newGeneration;
    }

    private boolean termConditionSatisfied(Generation generation) {
        double bestFitness = generation.getBestFitness();

        
        
        if(bestFitness > lastFitnessRemembered){
            lastFitnessRemembered = bestFitness;
            lastFitnessRememberedGeneration = generationCount;
        }
        
        
        if(lastFitnessRememberedGeneration < generationCount - 1000){
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
    /*
    
    private void doSelection(){
    throw new NotImplementedException();
    }
    
    private void doCrossover(){
    throw new NotImplementedException();
    }
    
    private void doMutate(){
    throw new NotImplementedException();
    }
    
     */
}
