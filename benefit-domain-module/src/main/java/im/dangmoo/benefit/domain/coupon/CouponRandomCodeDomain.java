package im.dangmoo.benefit.domain.coupon;

import java.security.SecureRandom;

public final class CouponRandomCodeDomain {

    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CouponRandomCodeDomain() {
    }

    public static String generate() {
        final char[] value = new char[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            value[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(value);
    }
}
