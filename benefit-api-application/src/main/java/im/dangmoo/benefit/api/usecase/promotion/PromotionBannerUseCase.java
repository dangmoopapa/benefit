package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.dto.promotion.PromotionBannerResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionBannerMongoRepository;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionBannerUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;
    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionBannerUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository,
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionBannerResponse banner(final String key) {
        final PromotionBannerDocument banner = promotionBannerMongoRepository.findByKey(key)
            .orElseThrow(ApiException::notFound);
        if (banner.getStatus().isNotActive()) {
            throw ApiException.notFound();
        }

        final Instant now = Instant.now();
        final List<PromotionBannerResponse.Item> items = new ArrayList<>();
        for (final var item : banner.sortedItems()) {
            final var policy = promotionPolicyMongoRepository.findByKey(item.getPolicyKey()).orElse(null);
            if (policy == null) {
                continue;
            }
            if (!PromotionPolicyDomain.of(policy).isOpenAt(now)) {
                continue;
            }
            items.add(PromotionBannerResponse.Item.of(item));
        }

        return PromotionBannerResponse.of(banner.getKey(), items);
    }
}
