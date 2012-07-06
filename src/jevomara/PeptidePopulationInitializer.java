/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apslab.cyclops.BitArrayGenoType;
import org.apslab.cyclops.IPopulationInitializer;
import org.apslab.cyclops.Individual;
import org.apslab.cyclops.Random;

/**
 *
 * @author andreas
 */
public class PeptidePopulationInitializer implements IPopulationInitializer {

    int peptideLength;
    int populationSize;
    private Individual prototypeIndividual;

    public PeptidePopulationInitializer setPeptideLength(int peptideLength) {
        this.peptideLength = peptideLength;
        return this;
    }

    public PeptidePopulationInitializer setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public List<Individual> getPopulation() {
        List<Individual> population = new ArrayList<Individual>();
        for (int i = 0; i < populationSize; i++) {
            StringBuilder seq = new StringBuilder();
            for (int j = 0; j < peptideLength; j++) {
                List<String> aminoAcids = new ArrayList<String>(AminoAcids.getAminoAcidAlphabet());
                Collections.shuffle(aminoAcids, Random.getInstance());
                seq.append(aminoAcids.get(0));
            }
            BitArrayGenoType genoType = new BitArrayGenoType().setChromosome(AminoAcids.singleLetterCodetoBinary(seq.toString()));
            Individual individual = prototypeIndividual.getInstance().setGenoType(genoType);
            population.add(individual);
        }
        return population;
    }

    public PeptidePopulationInitializer setPrototypeIndividual(Individual individual) {
        this.prototypeIndividual = individual;
        return this;
    }

}
