package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.balance.PointBalance;
import im.dangmoo.benefit.domain.data.point.balance.PointLot;

import java.time.Instant;
import java.util.List;

public record PointBalanceResponse(
    String id,
    String userId,
    long point,
    long available,
    List<PointLotResponse> lots,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PointBalanceResponse of(final PointBalance balance, final Instant now) {
        return new PointBalanceResponse(
            balance.getId(),
            balance.getUserId(),
            balance.getPoint(),
            balance.available(now),
            balance.getLots().stream().map(PointLotResponse::of).toList(),
            balance.getCreatedBy(),
            balance.getCreatedAt(),
            balance.getUpdatedBy(),
            balance.getUpdatedAt()
        );
    }

    public static PointBalanceResponse empty(final String userId) {
        return new PointBalanceResponse(null, userId, 0L, 0L, List.of(), null, null, null, null);
    }

    public record PointLotResponse(Instant expiresAt, long point) {
        public static PointLotResponse of(final PointLot lot) {
            return new PointLotResponse(lot.getExpiresAt(), lot.getPoint());
        }
    }
}
