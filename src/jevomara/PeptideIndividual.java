/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jevomara;

import org.apslab.cyclops.BitArrayGenoType;
import org.apslab.cyclops.Individual;

/**
 *
 * @author andreas
 */
public class PeptideIndividual extends Individual {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) super.getGenoType()).getChromosome()));
        sb.append(" ");
        sb.append("Fitness: ");
        sb.append(super.getFitness());
        return sb.toString();
    }

    @Override
    public PeptideIndividual getInstance() {
        return new PeptideIndividual();
    }
    
    @Override
    public boolean equals(Object obj) {
        //TO-DO: Safe-guard for type cast runtime error
        return AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) super.getGenoType()).getChromosome()).equals(AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) ((PeptideIndividual) obj).getGenoType()).getChromosome()));
    }
    
    @Override
    public int hashCode() {
        return AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) super.getGenoType()).getChromosome()).hashCode();
    }
    
    
}
