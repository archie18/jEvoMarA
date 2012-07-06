/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.util.List;
import org.apslab.cyclops.Fitness;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Individual;

/**
 *
 * @author andreas
 */
public class SequenceIdentityFitnessFunction implements IFitnessFunction {

    private String targetSequence;

    public SequenceIdentityFitnessFunction setTargetSequence(String targetSequence) {
        this.targetSequence = targetSequence;
        return this;
    }

    public Individual calculate(Individual individual) {
        String sequence = AminoAcids.binaryToSingleLetterCode((boolean[]) individual.getGenoType().getChromosome());
        int identityCount = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == targetSequence.charAt(i)) {
                identityCount++;
            }
        }
        Fitness fitness = new Fitness(1.0 / identityCount);
        individual.setFitness(fitness);
        return individual;
    }

    public List<Individual> calculate(List<Individual> individuals) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
