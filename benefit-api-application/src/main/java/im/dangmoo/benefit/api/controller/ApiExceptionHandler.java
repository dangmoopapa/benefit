package im.dangmoo.benefit.api.controller;

import im.dangmoo.benefit.api.usecase.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> handleApiException(final ApiException exception) {
        return ApiResponse.of(exception);
    }
}
