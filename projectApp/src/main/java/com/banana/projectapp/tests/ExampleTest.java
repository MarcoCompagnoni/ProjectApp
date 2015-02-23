package com.banana.projectapp.tests;

import android.test.InstrumentationTestCase;

/**
 * Created by Compagnoni on 21/01/2015.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
