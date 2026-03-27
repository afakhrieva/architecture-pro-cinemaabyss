package ru.yandex.practicum.arch.cinema.events.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Error> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Ошибка валидации" }
        logger.error("Ошибка валидации: $errors")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Error("Ошибка валидации: $errors"))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<Error> {
        logger.error("Ошибка чтения запроса: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Error("Некорректный формат запроса. Проверьте JSON."))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Error> {
        logger.error("Некорректный запрос: ${ex.message}")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Error(ex.message ?: "Некорректный запрос"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Error> {
        logger.error("Внутренняя ошибка сервера", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Error("Internal Server Error"))
    }
}