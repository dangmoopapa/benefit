package im.dangmoo.benefit.domain.data.point.transaction;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public final class PointTransactionCursor {

    private static final char SEPARATOR = '|';

    private PointTransactionCursor() {
    }

    public static String encode(final Instant transactionAt, final String id) {
        final String raw = Long.toString(transactionAt.toEpochMilli()) + SEPARATOR + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Decoded decode(final String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            final String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            final int sep = raw.indexOf(SEPARATOR);
            if (sep <= 0 || sep == raw.length() - 1) {
                return null;
            }
            final Instant transactionAt = Instant.ofEpochMilli(Long.parseLong(raw.substring(0, sep)));
            final String id = raw.substring(sep + 1);
            if (id.isBlank()) {
                return null;
            }
            return new Decoded(transactionAt, id);
        } catch (final IllegalArgumentException ignored) {
            return null;
        }
    }

    public record Decoded(Instant transactionAt, String id) {
    }
}
