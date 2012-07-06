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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) super.getGenoType()).getChromosome()));
        sb.append(" ");
        sb.append("Fitness: ");
        sb.append(super.getFitness());
        return sb.toString();
    }

    public PeptideIndividual getInstance() {
        return new PeptideIndividual();
    }    
}
