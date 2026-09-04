package im.dangmoo.benefit.admin.support;

public class ApiException extends RuntimeException {

    private final ApiMessage apiMessage;

    public ApiException(final ApiMessage apiMessage) {
        super(apiMessage.message());
        this.apiMessage = apiMessage;
    }

    public ApiMessage apiMessage() {
        return apiMessage;
    }
}
