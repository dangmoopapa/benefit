package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponApplyDomainTest {

    private static CouponApplyDomain domain(
        final List<String> productIds,
        final List<String> categoryIds,
        final List<String> brandIds,
        final String segmentId
    ) {
        return CouponApplyDomain.of(CouponApplyCondition.create(productIds, categoryIds, brandIds, segmentId));
    }

    @Nested
    @DisplayName("belongsTo")
    class BelongsTo {

        @Test
        @DisplayName("상품·브랜드 제한이 없으면 어떤 값도 소속되지 않는다")
        void emptyLists_false() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of(), null);
            assertThat(domain.belongsTo("p1", "b1")).isFalse();
        }

        @Test
        @DisplayName("상품 ID가 일치하면 소속이다")
        void productMatch_true() {
            final CouponApplyDomain domain = domain(List.of("p1", "p2"), List.of(), List.of(), null);
            assertThat(domain.belongsTo("p1", "other")).isTrue();
        }

        @Test
        @DisplayName("브랜드 ID가 일치하면 소속이다")
        void brandMatch_true() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of("b1"), null);
            assertThat(domain.belongsTo("other", "b1")).isTrue();
        }

        @Test
        @DisplayName("상품 또는 브랜드 중 하나만 맞아도 소속이다")
        void productOrBrand_true() {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of("b1"), null);
            assertThat(domain.belongsTo("p1", "x")).isTrue();
            assertThat(domain.belongsTo("x", "b1")).isTrue();
        }

        @Test
        @DisplayName("둘 다 틀리면 소속이 아니다")
        void neitherMatch_false() {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of("b1"), null);
            assertThat(domain.belongsTo("p9", "b9")).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("target 이 비어 있으면 contains 는 실패한다")
        void blankTarget_false(final String target) {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of("b1"), null);
            assertThat(domain.belongsTo(target, target)).isFalse();
        }
    }

    @Nested
    @DisplayName("isSatisfied")
    class IsSatisfied {

        @Test
        @DisplayName("제한이 전부 비어 있으면 통과한다")
        void noRestrictions_true() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of(), null);
            assertThat(domain.isSatisfied("p", "c", "b", "s")).isTrue();
        }

        @Test
        @DisplayName("상품 제한이 있고 일치하면 통과한다")
        void productAllowed_true() {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of(), null);
            assertThat(domain.isSatisfied("p1", null, null, null)).isTrue();
        }

        @Test
        @DisplayName("상품 제한이 있고 불일치하면 실패한다")
        void productDenied_false() {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of(), null);
            assertThat(domain.isSatisfied("p2", null, null, null)).isFalse();
        }

        @Test
        @DisplayName("카테고리 제한이 있고 불일치하면 실패한다")
        void categoryDenied_false() {
            final CouponApplyDomain domain = domain(List.of(), List.of("c1"), List.of(), null);
            assertThat(domain.isSatisfied(null, "c2", null, null)).isFalse();
        }

        @Test
        @DisplayName("브랜드 제한이 있고 불일치하면 실패한다")
        void brandDenied_false() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of("b1"), null);
            assertThat(domain.isSatisfied(null, null, "b2", null)).isFalse();
        }

        @Test
        @DisplayName("세그먼트가 설정돼 있고 요청과 다르면 실패한다")
        void segmentMismatch_false() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of(), "seg-a");
            assertThat(domain.isSatisfied(null, null, null, "seg-b")).isFalse();
        }

        @Test
        @DisplayName("세그먼트가 설정돼 있고 요청과 같으면 통과한다")
        void segmentMatch_true() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of(), "seg-a");
            assertThat(domain.isSatisfied(null, null, null, "seg-a")).isTrue();
        }

        @Test
        @DisplayName("세그먼트가 blank 면 세그먼트 검사를 건너뛴다")
        void blankSegment_skipped() {
            final CouponApplyDomain domain = domain(List.of(), List.of(), List.of(), "  ");
            assertThat(domain.isSatisfied(null, null, null, "anything")).isTrue();
        }

        @Test
        @DisplayName("상품·카테고리·브랜드·세그먼트를 모두 만족해야 통과한다")
        void allMustPass() {
            final CouponApplyDomain domain = domain(
                List.of("p1"),
                List.of("c1"),
                List.of("b1"),
                "seg"
            );
            assertThat(domain.isSatisfied("p1", "c1", "b1", "seg")).isTrue();
            assertThat(domain.isSatisfied("p1", "c1", "b9", "seg")).isFalse();
            assertThat(domain.isSatisfied("p1", "c9", "b1", "seg")).isFalse();
            assertThat(domain.isSatisfied("p9", "c1", "b1", "seg")).isFalse();
            assertThat(domain.isSatisfied("p1", "c1", "b1", "other")).isFalse();
        }

        @Test
        @DisplayName("제한 목록이 있는데 target 이 null/blank 이면 실패한다")
        void restrictedButBlankTarget_false() {
            final CouponApplyDomain domain = domain(List.of("p1"), List.of(), List.of(), null);
            assertThat(domain.isSatisfied(null, null, null, null)).isFalse();
            assertThat(domain.isSatisfied(" ", null, null, null)).isFalse();
        }
    }
}
