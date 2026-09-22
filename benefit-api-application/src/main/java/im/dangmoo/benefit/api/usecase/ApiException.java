package im.dangmoo.benefit.api.usecase;

public class ApiException extends RuntimeException {

    private final ApiMessage apiMessage;

    private ApiException(final ApiMessage apiMessage) {
        super(apiMessage.message());
        this.apiMessage = apiMessage;
    }

    public ApiMessage apiMessage() {
        return apiMessage;
    }

    public static ApiException notFound() {
        return new ApiException(ApiMessage.NOT_FOUND);
    }

    public static ApiException invalidStatus() {
        return new ApiException(ApiMessage.INVALID_STATUS);
    }

    public static ApiException conditionNotSatisfied() {
        return new ApiException(ApiMessage.CONDITION_NOT_SATISFIED);
    }

    public static ApiException alreadyIssuedCoupon() {
        return new ApiException(ApiMessage.ALREADY_ISSUED_COUPON);
    }

    public static ApiException stockExhausted() {
        return new ApiException(ApiMessage.STOCK_EXHAUSTED);
    }

    public static ApiException stockExhaustedCoupon() {
        return new ApiException(ApiMessage.STOCK_EXHAUSTED_COUPON);
    }

    public static ApiException policyIssueCoupon() {
        return new ApiException(ApiMessage.POLICY_ISSUE_COUPON);
    }

    public static ApiException insufficientPoint() {
        return new ApiException(ApiMessage.INSUFFICIENT_POINT);
    }

    public static ApiException preparingMembership() {
        return new ApiException(ApiMessage.PREPARING_MEMBERSHIP);
    }

    public static ApiException alreadyAppliedPromotion() {
        return new ApiException(ApiMessage.ALREADY_APPLIED_PROMOTION);
    }

    public static ApiException invalidPromotion() {
        return new ApiException(ApiMessage.INVALID_PROMOTION);
    }
}
