package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Обьект не найден " + e.getMessage());
        return new ErrorResponse("Объект не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateException(DuplicatedException e) {
        log.warn("Повторяющийся обьект-привело к ошибке");
        return new ErrorResponse("Дублирование объекта!");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(ValidationException e) {
        log.warn("Ошибка при валидации " + e.getMessage());
        return new ErrorResponse("Ошибка валидации,проверьте корректность данных");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidValidate(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации данных " + e.getMessage());
        return new ErrorResponse("Ошибка валидации данных");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidValidate(ConstraintViolationException e) {
        log.warn("Ошибка валидации данных " + e.getMessage());
        return new ErrorResponse("Ошибка валидации данных");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInvalidValidate(Exception e) {
        log.warn("Ошибка сервера " + e.getMessage());
        return new ErrorResponse("Ошибка сервера");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccessException(AccessException e) {
        log.warn("Ошибка  " + e.getMessage());
        return new ErrorResponse("Данная вещь арендуется");
    }


}

