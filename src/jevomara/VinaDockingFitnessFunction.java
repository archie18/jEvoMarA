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
import java.util.*;
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
    /** Cap peptides? (Acetylate N-terminus, amidate C-terminus) **/
    private boolean capping = false;

    /**
     * Returns true if peptides should be capped (acetylate N-terminus, amidate C-terminus).
     * @return true if peptides should be capped
     */
    public boolean isCapping() {
        return capping;
    }

    /**
     * Determines whether peptides should be capped (acetylate N-terminus, amidate C-terminus)
     * @param capping true if peptides should be capped
     * @return this object
     */
    public VinaDockingFitnessFunction setCapping(boolean capping) {
        this.capping = capping;
        return this;
    }

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
            // repeat until all lines are read
            while ((text = reader.readLine()) != null) {
                String[] split = text.split(dockingScoresSeparator);
                double fitness;
                try {
                    fitness = Double.parseDouble(split[1]);
                } catch (NumberFormatException ex) {
                    fitness = errorFitness;
                    log.debug("Error parsing docking score: " + text);
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
        
        // If capping is enabled, add leading and trailing underscores to
        // peptide sequence in order indicate their N-terminus should be
        // acetylated and their C-terminus should be amidated.
        //if (isCapping()) {
        if (true) {
            List<String> peptides2 = new ArrayList<String>();
            for (String peptide : peptides) {
                peptides2.add("_" + peptide + "_");
            }
            peptides = peptides2;
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
        
        // Uniquify list of selected peptides (maintains original order of elements)
        Set<String> tempSet = new HashSet<String>();
        List<String> tempList = new ArrayList<String>();
        for (String peptide : selectedPeptides) {
            if (!tempSet.contains(peptide)) {
                tempList.add(peptide);
                tempSet.add(peptide);
            }
        }
        selectedPeptides = tempList;
        
        // Dock selected peptides
        // Generate a work dir name
        String workDir = Long.toHexString(Double.doubleToLongBits(Random.getInstance().nextDouble()));
        log.trace("workDir={} peptides={}", workDir, selectedPeptides);

        // Build command line
        int numArgs = 3 + selectedPeptides.size();
        String[] args = new String[numArgs];
        int j = 0;
        args[j++] = dockingScript;
        args[j++] = "-d";
        args[j++] = workDir;
        for (int i = 0; i < selectedPeptides.size(); i++) {
            args[j + i] = selectedPeptides.get(i);
        }

        // Run external process
        try {
            ProcessBuilder pb = new ProcessBuilder(args).directory(new File(baseDir));
            Process process = pb.start();
            process.waitFor();
            
            // Retrieve stdout and stderr
            if (log.isTraceEnabled()) {
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                log.trace("Exit code: {}", process.exitValue());
                log.trace("Output of running {} is:", Arrays.toString(args));
                while ((line = stdout.readLine()) != null) {
                    log.trace(line);
                }
                log.trace("End of ouput.");
                log.trace("Stderr of running {} is:", Arrays.toString(args));
                while ((line = stderr.readLine()) != null) {
                    log.trace(line);
                }
                log.trace("End of stderr.");
            }

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
