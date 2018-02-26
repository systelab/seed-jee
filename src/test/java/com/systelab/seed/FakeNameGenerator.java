package com.systelab.seed;

import java.util.Random;

public class FakeNameGenerator {

    static Random rnd = new Random();// random is used for randomly select consonance and vowels from given list
    static final String CONS = "zcvsbnmljfdsrtyp"; //String which store the consonances
    static final String VOWELS = "aeiou";//String which store vowels

    public String generateName(boolean uppercase) //len define length of names
    {
        int len = rnd.nextInt(8) + 3;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
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
