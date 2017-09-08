package com.zuhlke.ta.twitterclient;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;

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
        LocationBounds bounds = new LocationBounds(expectedLatMin, expectedLongMin, expectedLatMax, expectedLongMax);

        assertThat(
                bounds.toLocationsArray(),
                arrayContaining(new double[][]{{expectedLatMin, expectedLongMin}, {expectedLatMax, expectedLongMax}}));
    }
}
