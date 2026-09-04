package im.dangmoo.benefit.admin.support;

public record ApiResponse<T>(
    Boolean success,
    String message,
    T body
) {

    public static <T> ApiResponse<T> of(final T body) {
        return new ApiResponse<>(true, ApiMessage.SUCCESS.message(), body);
    }

    public static <T> ApiResponse<T> of(final ApiMessage apiMessage) {
        return new ApiResponse<>(false, apiMessage.message(), null);
    }

    public static <T> ApiResponse<T> of(final ApiException exception) {
        return of(exception.apiMessage());
    }
}
