package com.zuhlke.ta.twitterclient;

/**
 * Created by eabi on 04/09/2017.
 */
public class LocationBounds {

    private final double latitudeMin;
    private final double longitudeMin;
    private final double latitudeMax;
    private final double longitudeMax;

    public LocationBounds(
            double latitudeMin,
            double longitudeMin,
            double latitudeMax,
            double longitudeMax) {

        this.latitudeMin = latitudeMin;
        this.longitudeMin = longitudeMin;
        this.latitudeMax = latitudeMax;
        this.longitudeMax = longitudeMax;
    }

    public double[][] toLocationsArray() {
        double[][] result = {{latitudeMin,longitudeMin},{latitudeMax,longitudeMax}};
        return result;
    }
}
