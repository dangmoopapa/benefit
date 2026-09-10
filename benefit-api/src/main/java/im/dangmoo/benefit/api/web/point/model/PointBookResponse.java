package im.dangmoo.benefit.api.web.point.model;

import im.dangmoo.benefit.domain.data.point.balance.PointBalance;
import im.dangmoo.benefit.domain.data.point.balance.PointLot;

import java.time.Instant;
import java.util.List;

public record PointBookResponse(
    String userId,
    long point,
    long available,
    List<PointLotResponse> lots,
    Instant updatedAt
) {

    public static PointBookResponse of(final PointBalance balance, final Instant now) {
        return new PointBookResponse(
            balance.getUserId(),
            balance.getPoint(),
            balance.available(now),
            balance.getLots().stream().map(PointLotResponse::of).toList(),
            balance.getUpdatedAt()
        );
    }

    public static PointBookResponse empty(final String userId) {
        return new PointBookResponse(userId, 0L, 0L, List.of(), null);
    }

    public record PointLotResponse(Instant expiresAt, long point) {
        public static PointLotResponse of(final PointLot lot) {
            return new PointLotResponse(lot.getExpiresAt(), lot.getPoint());
        }
    }
}
