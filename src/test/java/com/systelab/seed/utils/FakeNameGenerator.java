package com.systelab.seed.utils;

import java.util.Random;

public class FakeNameGenerator {

    private static Random rnd = new Random();// random is used for randomly select consonance and vowels from given list
    private static final String CONS = "zcvsbnmljfdsrtyp"; //String which store the consonances
    private static final String VOWELS = "aeiou";//String which store vowels

    public String generateName(boolean uppercase)
    {
        int length = rnd.nextInt(8) + 3;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0)
                sb.append(CONS.charAt(rnd.nextInt(CONS.length())));
            else sb.append(VOWELS.charAt(rnd.nextInt(VOWELS.length())));
        }
        if (uppercase) {
            String composed = sb.toString();
            return Character.toUpperCase(composed.charAt(0)) + composed.substring(1);
        } else {
            return sb.toString();
        }
    }
}
