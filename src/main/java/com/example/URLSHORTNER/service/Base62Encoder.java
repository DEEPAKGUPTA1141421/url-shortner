package com.example.URLSHORTNER.service;

public final class Base62Encoder {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final long BASE = 62L;
    private static final int CODE_LENGTH = 7;
    private static final long SPACE = pow(BASE, CODE_LENGTH); // 62^7

    // Arbitrary large prime, coprime with SPACE = 2^7 * 31^7 since it's
    // an odd prime other than 31. Swapping this changes the "shuffle" but not
    // the collision-free guarantee.
    private static final long MULTIPLIER = 1_500_450_271L;

    private Base62Encoder() {
    }

    public static String encode(long id) {
        if (id < 0 || id >= SPACE) {
            throw new IllegalArgumentException("id out of supported range for " + CODE_LENGTH + "-char codes: " + id);
        }
        long obfuscated = Math.floorMod(id * MULTIPLIER, SPACE);
        char[] out = new char[CODE_LENGTH];
        long n = obfuscated;
        for (int i = CODE_LENGTH - 1; i >= 0; i--) {
            out[i] = ALPHABET.charAt((int) (n % BASE));
            n /= BASE;
        }
        return new String(out);
    }

    private static long pow(long base, int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) {
            result *= base;
        }
        return result;
    }
}
