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
}
