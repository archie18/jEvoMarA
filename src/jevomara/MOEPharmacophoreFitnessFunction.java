/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apslab.cyclops.BitArrayGenoType;
import org.apslab.cyclops.Fitness;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Individual;

/**
 *
 * @author andreas
 */
public class MOEPharmacophoreFitnessFunction implements IFitnessFunction {

    private String[] args = new String[] {"moebatch", "-licwait", "-load", "conf_s.svl", "-load", "evoPh4Search.svl", "-exec", "evoPh4Search [${peptides}]"};
    private String workDir = "/home/andreas/Documents/evoPh4";
    //private String workDir = "/Users/andreas/Documents/evoPh4";
    private int noHitFitness = 1000;

    public Individual calculate(Individual individual) {

        String peptide = AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) individual.getGenoType()).getChromosome());
        double fitness = noHitFitness;

        // Build command line
        StringBuilder sb = new StringBuilder();
        Map<String, String> substitutionMap = new HashMap<String, String>();
        substitutionMap.put("peptides", sb.append("\"").append(peptide).append("\"").toString());
        String[] parsedArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            for (String key : substitutionMap.keySet()) {
                sb.setLength(0);
                parsedArgs[i] = args[i].replace(sb.append("${").append(key).append("}").toString(), substitutionMap.get(key));
            }
        }

        // Run external process
        try {
            ProcessBuilder pb = new ProcessBuilder(parsedArgs).directory(new File(workDir));
            //Map<String, String> env = pb.environment();
            //env.put("MOE", "/Applications/moe2010.10");
            Process process = pb.start();
            process.waitFor();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
//            System.out.println("Exit code: " + process.exitValue());
//            System.out.printf("Output of running %s is:", Arrays.toString(parsedArgs));
//            while ((line = stdout.readLine()) != null) {
//                System.out.println(line);
//            }
//            System.out.println("");
//            System.out.printf("Stderr of running %s is:", Arrays.toString(parsedArgs));
//            while ((line = stderr.readLine()) != null) {
//                System.out.println(line);
//            }
//            System.out.println("");

            // Parse output
            while ((line = stdout.readLine()) != null) {
                String[] fields = line.split("\t");
                String matchedPeptide = fields[0];
                double rmsd = Double.parseDouble(fields[1]);
                int mseq = Integer.parseInt(fields[2]);

                if (peptide.equals(matchedPeptide) && rmsd < fitness) {
                    fitness = rmsd;
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MOEPharmacophoreFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MOEPharmacophoreFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        }

        return individual.setFitness(new Fitness(fitness));
    }

    public List<Individual> calculate(List<Individual> individuals) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
