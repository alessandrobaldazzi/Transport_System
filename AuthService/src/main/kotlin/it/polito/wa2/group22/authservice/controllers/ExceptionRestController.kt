package it.polito.wa2.group22.authservice.controllers

import it.polito.wa2.group22.authservice.dto.ErrorMessageDTO
import it.polito.wa2.group22.authservice.exceptions.AuthServiceBadRequestException
import it.polito.wa2.group22.authservice.exceptions.AuthServiceNotFoundException
import it.polito.wa2.group22.authservice.exceptions.AuthServicePanicException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionRestController {

    @ExceptionHandler(AuthServiceNotFoundException::class)
    fun handleValidationException(ex: AuthServiceNotFoundException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AuthServicePanicException::class)
    fun handleValidationException(ex: AuthServicePanicException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AuthServiceBadRequestException::class)
    fun handleValidationException(ex: AuthServiceBadRequestException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.BAD_REQUEST.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}

