package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponApplyDomainTest {

    @Test
    @DisplayName("상품·브랜드 제한이 없으면 어떤 값도 소속되지 않는다")
    void covers_emptyLists() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.covers("p1", "b1")).isFalse();
    }

    @Test
    @DisplayName("상품 ID가 일치하면 소속이다")
    void covers_productMatch() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1", "p2"), List.of(), List.of(), null)
        );
        assertThat(domain.covers("p1", "other")).isTrue();
    }

    @Test
    @DisplayName("브랜드 ID가 일치하면 소속이다")
    void covers_brandMatch() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of("b1"), null)
        );
        assertThat(domain.covers("other", "b1")).isTrue();
    }

    @Test
    @DisplayName("상품 또는 브랜드 중 하나라도 일치하면 소속이다")
    void covers_productOrBrand() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of("b1"), null)
        );
        assertThat(domain.covers("p1", "x")).isTrue();
        assertThat(domain.covers("x", "b1")).isTrue();
    }

    @Test
    @DisplayName("상품·브랜드가 모두 불일치하면 소속이 아니다")
    void covers_neitherMatch() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of("b1"), null)
        );
        assertThat(domain.covers("p2", "b2")).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("대상이 blank 이면 소속이 아니다")
    void covers_blankTarget(final String blank) {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of("b1"), null)
        );
        assertThat(domain.covers(blank, blank)).isFalse();
    }

    @Test
    @DisplayName("적용 제한이 없으면 통과한다")
    void isApplicableTo_noRestrictions() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", "s1")).isTrue();
    }

    @Test
    @DisplayName("허용 상품이면 통과한다")
    void isApplicableTo_productAllowed() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of(), null)
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", null)).isTrue();
    }

    @Test
    @DisplayName("허용되지 않은 상품이면 실패한다")
    void isApplicableTo_productDenied() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of(), null)
        );
        assertThat(domain.isApplicableTo("p2", "c1", "b1", null)).isFalse();
    }

    @Test
    @DisplayName("허용되지 않은 카테고리면 실패한다")
    void isApplicableTo_categoryDenied() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of("c1"), List.of(), null)
        );
        assertThat(domain.isApplicableTo("p1", "c2", "b1", null)).isFalse();
    }

    @Test
    @DisplayName("허용되지 않은 브랜드면 실패한다")
    void isApplicableTo_brandDenied() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of("b1"), null)
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b2", null)).isFalse();
    }

    @Test
    @DisplayName("세그먼트가 다르면 실패한다")
    void isApplicableTo_segmentMismatch() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of(), "vip")
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", "normal")).isFalse();
    }

    @Test
    @DisplayName("세그먼트가 일치하면 통과한다")
    void isApplicableTo_segmentMatch() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of(), "vip")
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", "vip")).isTrue();
    }

    @Test
    @DisplayName("정책에 세그먼트가 있으면 요청 세그먼트가 blank 여도 실패한다")
    void isApplicableTo_blankRequestSegmentFails() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of(), List.of(), List.of(), "vip")
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", null)).isFalse();
        assertThat(domain.isApplicableTo("p1", "c1", "b1", "  ")).isFalse();
    }

    @Test
    @DisplayName("상품·카테고리·브랜드·세그먼트를 모두 만족해야 통과한다")
    void isApplicableTo_allMustPass() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of("c1"), List.of("b1"), "vip")
        );
        assertThat(domain.isApplicableTo("p1", "c1", "b1", "vip")).isTrue();
        assertThat(domain.isApplicableTo("p1", "c1", "b2", "vip")).isFalse();
    }

    @Test
    @DisplayName("제한이 있어도 대상이 blank 이면 실패한다")
    void isApplicableTo_restrictedButBlankTarget() {
        final CouponApplyDomain domain = CouponApplyDomain.of(
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of(), null)
        );
        assertThat(domain.isApplicableTo(null, "c1", "b1", null)).isFalse();
    }
}
