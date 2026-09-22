package im.dangmoo.benefit.admin.usecase;

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

    public static ApiException duplicateKey() {
        return new ApiException(ApiMessage.DUPLICATE_KEY);
    }

    public static ApiException invalidStatus() {
        return new ApiException(ApiMessage.INVALID_STATUS);
    }

    public static ApiException conditionNotSatisfied() {
        return new ApiException(ApiMessage.CONDITION_NOT_SATISFIED);
    }

    public static ApiException insufficientPoint() {
        return new ApiException(ApiMessage.INSUFFICIENT_POINT);
    }

    public static ApiException stockExhausted() {
        return new ApiException(ApiMessage.STOCK_EXHAUSTED);
    }

    public static ApiException preparingMembership() {
        return new ApiException(ApiMessage.PREPARING_MEMBERSHIP);
    }

    public static ApiException invalidPromotionFeature() {
        return new ApiException(ApiMessage.INVALID_PROMOTION_FEATURE);
    }

    public static ApiException alreadyDrawnPromotion() {
        return new ApiException(ApiMessage.ALREADY_DRAWN_PROMOTION);
    }

    public static ApiException lotteryNotReady() {
        return new ApiException(ApiMessage.LOTTERY_NOT_READY);
    }
}
