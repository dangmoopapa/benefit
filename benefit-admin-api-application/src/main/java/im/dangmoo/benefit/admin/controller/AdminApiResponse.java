package im.dangmoo.benefit.admin.controller;

import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.admin.usecase.ApiMessage;

public record AdminApiResponse<T>(
    Boolean success,
    String message,
    T body
) {

    public static <T> AdminApiResponse<T> of(final T body) {
        return new AdminApiResponse<>(true, ApiMessage.SUCCESS.message(), body);
    }

    public static <T> AdminApiResponse<T> of(final ApiMessage apiMessage) {
        return new AdminApiResponse<>(false, apiMessage.message(), null);
    }

    public static <T> AdminApiResponse<T> of(final ApiException exception) {
        return of(exception.apiMessage());
    }
}
