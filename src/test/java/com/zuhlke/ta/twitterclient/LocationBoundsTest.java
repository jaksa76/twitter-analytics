package com.zuhlke.ta.twitterclient;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by eabi on 04/09/2017.
 */
public class LocationBoundsTest {
    @Test
    public void toLocationsArray_shouldReturnArrayOfCoordinates() {
        double expectedLatMin = 3.75;
        double expectedLongMin = 2.5;
        double expectedLatMax = 13.75;
        double expectedLongMax = 15.0;
        LocationBounds bounds = new LocationBounds(
                expectedLatMin,
                expectedLongMin,
                expectedLatMax,
                expectedLongMax);

        double[][] expected = {{expectedLatMin, expectedLongMin}, {expectedLatMax, expectedLongMax}};
        assertArrayEquals(expected, bounds.toLocationsArray());
    }
}
