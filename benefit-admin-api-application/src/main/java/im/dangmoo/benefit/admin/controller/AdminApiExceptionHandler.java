package im.dangmoo.benefit.admin.controller;

import im.dangmoo.benefit.admin.usecase.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdminApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.OK)
    AdminApiResponse<Void> handleApiException(final ApiException exception) {
        return AdminApiResponse.of(exception);
    }
}
