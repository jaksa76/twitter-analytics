package com.zuhlke.ta.prototype.solutions.bigquery;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MapUtilsTest {
    @Test
    public void testMErgingSomeStuff() {
        Map<String, Integer> left = new HashMap<>();
        Map<String, Integer> right = new HashMap<>();

        left.put("1", 1);
        right.put("2", 3);
        left.put("3", 4);
        right.put("3", 4);
        left.put("5", 5);
        right.put("5", 6);
        right.put("6", 7);

        System.out.println(MapUtils.merge(left, right));
    }

}