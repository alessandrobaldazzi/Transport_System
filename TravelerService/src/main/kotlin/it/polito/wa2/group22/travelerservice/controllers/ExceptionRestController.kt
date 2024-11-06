package it.polito.wa2.group22.travelerservice.controllers

import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceNotFoundException
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceServicePanicException
import it.polito.wa2.group22.travelerservice.dto.ErrorMessageDTO
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceBadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionRestController {

    @ExceptionHandler(TravelerServiceNotFoundException::class)
    fun handleValidationException(ex: TravelerServiceNotFoundException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.NOT_FOUND.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(TravelerServiceServicePanicException::class)
    fun handleValidationException(ex: TravelerServiceServicePanicException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(TravelerServiceBadRequestException::class)
    fun handleValidationException(ex: TravelerServiceBadRequestException): ResponseEntity<ErrorMessageDTO> {
        val errorMessage = ErrorMessageDTO(
            HttpStatus.BAD_REQUEST.value(),
            ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

}