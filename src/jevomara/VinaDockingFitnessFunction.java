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

    private String dockingScript;
    private String dockingScoresFile = "bestranking.lst";
    private String dockingScoresSeparator = "\t";
    /** "vina" for AutoDock Vina or "gold" for GOLD */
    private String dockingAlgorithm = "vina";
    private String baseDir;
    //private String baseDir = "/Users/andreas/Documents/evoVina/1BL0_docking";
    private Map<String, Double> dockingScores = new HashMap<String, Double>();
    private double errorFitness = 1000;
    /** N-terminal cap **/
    private String NCap;
    /** C-terminal cap **/
    private String CCap;

    /**
     * Returns the N-terminal cap (Ac- = Acetyl, Bzim- = 2-benzimidazolyl).
     * @return the N-terminal cap
     */
    public String getNCap() {
        return NCap;
    }

    /**
     * Sets the N-terminal cap (Ac- = Acetyl, Bzim- = 2-benzimidazolyl).
     * @param NCap the N-terminal cap
     * @return this object
     */
    public VinaDockingFitnessFunction setNCap(String NCap) {
        this.NCap = NCap;
        return this;
    }

    /**
     * Returns the C-terminal cap (NMe- = N-methyl (amidation)).
     * @return the C-terminal cap
     */
    public String getCCap() {
        return CCap;
    }

    /**
     * Sets the C-terminal cap (NMe- = N-methyl (amidation))
     * @param CCap the C-terminal cap
     * @return this object
     */
    public VinaDockingFitnessFunction setCCap(String CCap) {
        this.CCap = CCap;
        return this;
    }

    /**
     * Return docking script path
     * @return docking script path
     */
    public String getDockingScript() {
        return dockingScript;
    }

    /**
     * Sets the path to the docking script
     * @param dockingScript path to the docking script
     * @return this object
     */
    public VinaDockingFitnessFunction setDockingScript(String dockingScript) {
        this.dockingScript = dockingScript;
        return this;
    }

    /**
     * Use which docking algorithm?
     * "vina" for AutoDock Vina or "gold" for GOLD.
     * @return "vina" for AutoDock Vina or "gold" for GOLD
     */
    public String getDockingAlgorithm() {
        return dockingAlgorithm;
    }

    /**
     * Use which docking algorithm?
     * "vina" for AutoDock Vina or "gold" for GOLD. "vina" is the default.
     * @param dockingAlgorithm "vina" for AutoDock Vina or "gold" for GOLD
     * @return this object
     */
    public VinaDockingFitnessFunction setDockingAlgorithm(String dockingAlgorithm) {
        this.dockingAlgorithm = dockingAlgorithm;
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
        
        // If cap is not null, add cap to peptide sequence
        if (NCap != null || CCap != null) {
            List<String> peptides2 = new ArrayList<String>();
            for (String peptide : peptides) {
                if (NCap != null) {
                    peptide = NCap + peptide;
                }
                if (CCap != null) {
                    peptide = peptide + CCap;
                }
                peptides2.add(peptide);
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
        if ("gold".equals(dockingAlgorithm)) {
            numArgs++;
        }
        String[] args = new String[numArgs];
        int j = 0;
        args[j++] = dockingScript;
        args[j++] = "-d";
        args[j++] = workDir;
        if ("gold".equals(dockingAlgorithm)) {
            args[j++] = "-g";
        }
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
