package edu.ncsu.csc.itrust.unit.action;

import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import junit.framework.TestCase;

public class RequestBiosurveillanceActionTest extends TestCase {

    private TestDataGenerator gen;

    protected void setUp() throws Exception {
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();
    }
}
