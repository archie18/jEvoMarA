/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.util.Arrays;
import java.util.List;
import org.apslab.cyclops.BitArrayRandomPopulationInitializer;
import org.apslab.cyclops.Fitness;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.IGeneticOperator;
import org.apslab.cyclops.IPopulationInitializer;
import org.apslab.cyclops.ISelector;
import org.apslab.cyclops.ITerminationCriterion;
import org.apslab.cyclops.Individual;
import org.apslab.cyclops.Mutation;
import org.apslab.cyclops.Random;
import org.apslab.cyclops.MaxIterationsMinFitnessTerminationCriterion;
import org.apslab.cyclops.MaxIterationsTerminationCriterion;
import org.apslab.cyclops.MinFitnessTerminationCriterion;
import org.apslab.cyclops.NullCrossOver;
import org.apslab.cyclops.Optimizer;
import org.apslab.cyclops.SwissArmyKnife;
import org.apslab.cyclops.ThreadedOptimizer;
import org.apslab.cyclops.TournamentSelection;
import org.apslab.cyclops.TwoPointCrossOver;

/**
 *
 * @author andreas
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       //System.out.println(Arrays.toString(AminoAcids.singleLetterCodetoBinary("ADAE")));
       //Long seed = 2L;
       Long seed = 1L;
       //Long seed = null;
       int iterations = 100;
       int populationSize = 10;
       int elitism = 1;
       //int tournamentSize = 2;
       int tournamentSize = 3;
       double mutationProbability = 0.5;
       int mutationNumGenes = 5;
       double minFitness = -12.0;
       int numberOfThreads = 3;
       int peptideLength = 5;
       int geneLength = 5; // bits
       //String baseDir = "/home/andreas/Documents/evoVina/1BL0_docking_wDNA";
       String baseDir = "/home/andreas/Documents/evoVina/1BL0_docking_BoxA";


       // Initilize random number generator
       Random.setSeed(seed);

       IPopulationInitializer populationInitializer = new BitArrayRandomPopulationInitializer().setPopulationSize(populationSize).setPrototypeIndividual(new PeptideIndividual()).setArrayLength(peptideLength * geneLength);
       List<Individual> population = populationInitializer.getPopulation();
       IGeneticOperator crossOver = new TwoPointCrossOver();
       //IGeneticOperator crossOver = new NullCrossOver();
       Mutation mutation = new Mutation().setProbability(mutationProbability).setN(mutationNumGenes);
       ISelector selection = new TournamentSelection().setTournamentSize(tournamentSize);
       //IFitnessFunction fitnessFunction = new SequenceIdentityFitnessFunction().setTargetSequence("RRRR");
       //IFitnessFunction fitnessFunction = new MOEPharmacophoreFitnessFunction();
       IFitnessFunction fitnessFunction = new VinaDockingFitnessFunction().setBaseDir(baseDir);
       ITerminationCriterion terminationCriterion = new MaxIterationsMinFitnessTerminationCriterion().setMaxIterations(iterations).setMinFitness(new Fitness(minFitness));
       //ITerminationCriterion terminationCriterion = new MaxIterationsTerminationCriterion().setMaxIterations(iterations);
       //ITerminationCriterion terminationCriterion = new MinFitnessTerminationCriterion().setMinFitness(new Fitness(minFitness));

       Optimizer optimizer = new Optimizer().setElitism(elitism);
       //ThreadedOptimizer optimizer = new ThreadedOptimizer().setElitism(elitism).setNumberOfThreads(numberOfThreads);
       optimizer.setPopulationInitializer(populationInitializer);
       optimizer.setCrossOverOperator(crossOver);
       optimizer.setMutationOperator(mutation);
       optimizer.setSelector(selection);
       optimizer.setFitnessFunction(fitnessFunction);
       optimizer.setTerminationCriterion(terminationCriterion);

       optimizer.optimize();
    }

}
