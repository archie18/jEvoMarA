/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apslab.cyclops.BitArrayGenoType;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Individual;

/**
 *
 * @author andreas
 */
public class SystematicOptimizer {
    private IFitnessFunction fitnessFunction;
    private int sequenceLength;
    private Individual prototypeIndividual;
    private int alphabetLength = AminoAcids.getAminoAcidAlphabet().size();
    private int populationSize;

    public SystematicOptimizer setFitnessFunction(IFitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        return this;
    }

    public SystematicOptimizer setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
        return this;
    }

    public SystematicOptimizer setPrototypeIndividual(Individual individual) {
        this.prototypeIndividual = individual;
        return this;
    }

    public SystematicOptimizer setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public void optimize() {
        Individual overallTop = null;
        List<Individual> allIndividuals = new ArrayList<Individual>();

        List<Individual> population = new ArrayList<Individual>();
        int numCombinations = (int) Math.pow(alphabetLength, sequenceLength);
        for (int combinationsIdx = 0; combinationsIdx < numCombinations; combinationsIdx++) {
            // Generate peptide sequence
            StringBuilder sequenceBuilder = new StringBuilder();
            for (int arrayIdx = sequenceLength-1; arrayIdx >= 0 ; arrayIdx--) {
                int aminoAcidIdx = (combinationsIdx / (int) Math.pow(alphabetLength, arrayIdx)) % alphabetLength;
                sequenceBuilder.append(AminoAcids.getAminoAcidAlphabet().get(aminoAcidIdx));
            }
            
            //System.out.println(sequenceBuilder.toString());

            // Create genotype and individual
            BitArrayGenoType genoType = new BitArrayGenoType().setChromosome(AminoAcids.singleLetterCodetoBinary(sequenceBuilder.toString()));
            Individual individual = prototypeIndividual.getInstance().setGenoType(genoType);
            population.add(individual);

            // Evaluate population
            if (population.size() >= populationSize) {
                // Logging
                System.out.println("Iteration: " + (combinationsIdx + 1));

                // Call fitness function
                population = fitnessFunction.calculate(population);

                // Logging
                if (overallTop == null) {
                    overallTop = population.get(0);
                }
                List<Individual> populationCpy = new ArrayList<Individual>(population);
                Collections.sort(populationCpy);
                if (populationCpy.get(0).compareTo(overallTop) < 0) {
                    overallTop = populationCpy.get(0);
                }

                // Logging
                for (Individual ind : population) {
                    System.out.println(ind);
                }
                System.out.println("Overall top: " + overallTop);

                allIndividuals.addAll(population);
                population = new ArrayList<Individual>();
            }
        }
        // Final logging
        System.out.println("Done.");
        for (Individual ind : allIndividuals) {
            System.out.println(AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) ind.getGenoType()).getChromosome()) + "\t" + ind.getFitness());
        }
    }

}
