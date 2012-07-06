/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apslab.cyclops.BitArrayGenoType;
import org.apslab.cyclops.Fitness;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Individual;
import org.apslab.cyclops.Random;
import org.apslab.cyclops.SwissArmyKnife;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andreas
 */
public class VinaDockingFitnessFunction implements IFitnessFunction {

    /** slf4j logging */
    private static final Logger log = LoggerFactory.getLogger(VinaDockingFitnessFunction.class);

    private String dockingScript = "/home/andreas/Documents/evoVina/evoVinaDock.sh";
    private String dockingScoresFile = "bestranking.lst";
    private String dockingScoresSeparator = "\t";
    private String baseDir;
    //private String baseDir = "/Users/andreas/Documents/evoVina/1BL0_docking";
    private Map<String, Double> dockingScores = new HashMap<String, Double>();
    private double errorFitness = 1000;

    public static boolean removeDirectory(File directory) {

      // System.out.println("removeDirectory " + directory);

      if (directory == null)
        return false;
      if (!directory.exists())
        return true;
      if (!directory.isDirectory())
        return false;

      String[] list = directory.list();

      // Some JVMs return null for File.list() when the
      // directory is empty.
      if (list != null) {
        for (int i = 0; i < list.length; i++) {
          File entry = new File(directory, list[i]);

          //        System.out.println("\tremoving entry " + entry);

          if (entry.isDirectory())
          {
            if (!removeDirectory(entry))
              return false;
          }
          else
          {
            if (!entry.delete())
              return false;
          }
        }
      }

      return directory.delete();
    }

    public VinaDockingFitnessFunction setBaseDir(String baseDir) {
        this.baseDir = baseDir;
        return this;
    }

    private void readDockingScores(String dockingScoresFile) {
        BufferedReader reader = null;
        try {
            File file = new File(dockingScoresFile);
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                String[] split = text.split(dockingScoresSeparator);
                double fitness;
                try {
                    fitness = Double.parseDouble(split[1]);
                } catch (NumberFormatException ex) {
                    fitness = errorFitness;
                }
                dockingScores.put(split[0], fitness);
            }
        } catch (FileNotFoundException ex) {
            log.error(ex.toString(), ex);
        } catch (IOException ex) {
            log.error(ex.toString(), ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
            log.error(ex.toString(), ex);
            }
        }
    }

    public List<Individual> calculate(List<Individual> individuals) {

        // Convert all BitArrayGenoTypes to Strings
        List<String> peptides = new ArrayList<String>();
        for (Individual individual : individuals) {
            peptides.add(AminoAcids.binaryToSingleLetterCode(((BitArrayGenoType) individual.getGenoType()).getChromosome()));
        }

        // Select peptides without fitness
        List<String> selectedPeptides = new ArrayList<String>();
        for (String peptide : peptides) {
            if (dockingScores.containsKey(peptide)) {
                // Use cached fitness value
                log.trace("Skipping peptide {}. Using cached fitness.", peptide);
            } else {
                selectedPeptides.add(peptide);
            }
        }
        
        // Dock selected peptides
        // Generate a work dir name
        String workDir = Long.toHexString(Double.doubleToLongBits(Random.getInstance().nextDouble()));
        log.trace("workDir={} peptides={}", workDir, peptides);

        // Build command line
        String[] args = new String[3 + peptides.size()];
        args[0] = dockingScript;
        args[1] = "-d";
        args[2] = workDir;
        for (int i = 0; i < peptides.size(); i++) {
            args[3 + i] = peptides.get(i);
        }

        // Run external process
        try {
            ProcessBuilder pb = new ProcessBuilder(args).directory(new File(baseDir));
            Process process = pb.start();
            process.waitFor();
//            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String line;
//            System.out.println("Exit code: " + process.exitValue());
//            System.out.printf("Output of running %s is:", Arrays.toString(args));
//            while ((line = stdout.readLine()) != null) {
//                System.out.println(line);
//            }
//            System.out.println("");
//            System.out.printf("Stderr of running %s is:", Arrays.toString(args));
//            while ((line = stderr.readLine()) != null) {
//                System.out.println(line);
//            }
//            System.out.println("");

            // Parse docking scores output file
            String scoresFiles = baseDir + "/" + workDir + "/" + dockingScoresFile;
            readDockingScores(scoresFiles);

            // Delete workDir
            this.removeDirectory(new File(baseDir + "/" + workDir));

        } catch (InterruptedException ex) {
            log.error(ex.toString(), ex);
        } catch (IOException ex) {
            log.error(ex.toString(), ex);
        }

        // Update fitness values of individuals
        for (int i = 0; i < individuals.size(); i++) {
            double fitness = dockingScores.get(peptides.get(i));
            individuals.get(i).setFitness(new Fitness(fitness));
        }

        return individuals;
    }

    public Individual calculate(Individual individual) {
        return calculate(SwissArmyKnife.createPopulation(individual)).get(0);
    }

}
