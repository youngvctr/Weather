package zerobase.weather.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.weather.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Exception handleServerException() {
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public RuntimeException handleClientException() {
        System.out.println("Bad Request");
        return new RuntimeException();
    }

    @ExceptionHandler(DiaryException.class)
    public ErrorResponse handleDiaryException(DiaryException e) {
        log.error("{} is occurred.", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
}
