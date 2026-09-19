package im.dangmoo.benefit.domain.coupon;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static List<String> generate(final int quantity) {
        if (quantity <= 0) {
            return List.of();
        }
        final Set<String> codes = HashSet.newHashSet(quantity);
        while (codes.size() < quantity) {
            codes.add(generate());
        }
        return new ArrayList<>(codes);
    }

    public static int length() {
        return LENGTH;
    }

    public static boolean isValidFormat(final String code) {
        if (code == null || code.length() != LENGTH) {
            return false;
        }
        for (int i = 0; i < code.length(); i++) {
            if (indexOf(code.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    private static int indexOf(final char value) {
        for (int i = 0; i < ALPHABET.length; i++) {
            if (ALPHABET[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
