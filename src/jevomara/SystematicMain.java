/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Optimizer;
import org.apslab.cyclops.RandomFitnessFunction;

/**
 *
 * @author andreas
 */
public class SystematicMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       int populationSize = 10;
       int peptideLength = 3; // amino acids
       int geneLength = 5; // bits
       String baseDir = "/home/andreas/Documents/evoVina/1BL0_docking_wDNA_systematic";

       IFitnessFunction fitnessFunction = new VinaDockingFitnessFunction().setBaseDir(baseDir);
       //IFitnessFunction fitnessFunction = new RandomFitnessFunction();

       SystematicOptimizer optimizer = new SystematicOptimizer();
       optimizer.setSequenceLength(peptideLength).setPopulationSize(populationSize);
       optimizer.setPrototypeIndividual(new PeptideIndividual());
       optimizer.setFitnessFunction(fitnessFunction);

       optimizer.optimize();
    }

}
