package com.flipkart.storm.mysql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MySqlBinLogSpoutTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MySqlBinLogSpoutTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MySqlBinLogSpoutTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
