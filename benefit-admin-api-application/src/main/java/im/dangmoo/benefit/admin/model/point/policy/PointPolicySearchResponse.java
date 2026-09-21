package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyStatus;

import java.time.Instant;
import java.util.List;

public record PointPolicySearchResponse(List<Item> items) {

    public static PointPolicySearchResponse of(final List<PointPolicy> policies) {
        return new PointPolicySearchResponse(policies.stream().map(Item::of).toList());
    }

    public record Item(
        String id,
        String key,
        String name,
        PointPolicyStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {

        public static Item of(final PointPolicy policy) {
            return new Item(
                policy.getId(),
                policy.getKey(),
                policy.getName(),
                policy.getStatus(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
            );
        }
    }
}
