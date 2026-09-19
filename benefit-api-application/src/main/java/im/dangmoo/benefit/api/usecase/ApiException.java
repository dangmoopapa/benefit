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

    public static ApiException alreadyIssued() {
        return new ApiException(ApiMessage.ALREADY_ISSUED);
    }

    public static ApiException stockExhausted() {
        return new ApiException(ApiMessage.STOCK_EXHAUSTED);
    }

    public static ApiException policyIssue() {
        return new ApiException(ApiMessage.POLICY_ISSUE);
    }
}
