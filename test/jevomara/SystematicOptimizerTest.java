/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jevomara;

import org.apslab.cyclops.RandomFitnessFunction;
import org.apslab.cyclops.IFitnessFunction;
import org.apslab.cyclops.Individual;
import org.apslab.cyclops.NullFitnessFunction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andreas
 */
public class SystematicOptimizerTest {

    public SystematicOptimizerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of optimize method, of class SystematicOptimizer.
     */
    @Test
    public void testOptimize() {
        System.out.println("optimize");
        SystematicOptimizer instance = new SystematicOptimizer();
        instance.setSequenceLength(3).setPopulationSize(10).setPrototypeIndividual(new PeptideIndividual()).setFitnessFunction(new RandomFitnessFunction());
        instance.optimize();
    }

}