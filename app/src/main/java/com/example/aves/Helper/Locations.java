package com.example.aves.Helper;

import java.util.ArrayList;
import java.util.Random;

public class Locations {
    public static Random rand = new Random();
    public static String locations[] = {
            "9.033140,38.750080",
            "8.989862,38.787562",
            "8.997847,38.759307",
            "9.009143,38.746488",
            "8.990170,38.725823"
    };

    public static String getLocation(){
        int i = rand.nextInt(locations.length);
        return locations[i];
    }
}
