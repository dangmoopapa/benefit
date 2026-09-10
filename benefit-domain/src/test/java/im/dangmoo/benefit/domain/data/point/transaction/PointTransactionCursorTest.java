package im.dangmoo.benefit.domain.data.point.transaction;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PointTransactionCursorTest {

    @Test
    void encodeDecodeRoundTrip() {
        final Instant at = Instant.parse("2026-09-11T04:00:00Z");
        final String cursor = PointTransactionCursor.encode(at, "abc123");
        final PointTransactionCursor.Decoded decoded = PointTransactionCursor.decode(cursor);
        assertEquals(at, decoded.transactionAt());
        assertEquals("abc123", decoded.id());
    }

    @Test
    void blankOrInvalidReturnsNull() {
        assertNull(PointTransactionCursor.decode(null));
        assertNull(PointTransactionCursor.decode(""));
        assertNull(PointTransactionCursor.decode("not-a-cursor"));
    }
}
