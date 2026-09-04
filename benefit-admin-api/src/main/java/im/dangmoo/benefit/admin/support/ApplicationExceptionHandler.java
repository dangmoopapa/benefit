package im.dangmoo.benefit.admin.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<Void> handleApiException(final ApiException exception) {
        return ApiResponse.of(exception);
    }
}
