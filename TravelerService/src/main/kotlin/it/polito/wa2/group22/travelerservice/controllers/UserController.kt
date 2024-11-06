package it.polito.wa2.group22.travelerservice.controllers

import it.polito.wa2.group22.travelerservice.dto.TicketsToGenerateDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.interfaces.*
import it.polito.wa2.group22.travelerservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("v1/profile")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    fun getUserProfile(
        @RequestHeader("Authorization") jwt: String)
    : ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.getUserProfile(principal.toString())
        return ResponseEntity.status(HttpStatus.OK).body(res.userDetails)
    }



    @PutMapping("v1/profile")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    fun updateUserProfile(
        @RequestHeader("Authorization") jwt: String,
        @RequestBody reqBody: UserProfileDTO)
    : ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.updateUserProfile(reqBody, principal.toString())
        return ResponseEntity.status(HttpStatus.CREATED).body("")
    }

    @GetMapping("v1/tickets")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    fun getUserTickets(@RequestHeader("Authorization") jwt: String)
    : ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.getUserTickets(principal.toString())
        return ResponseEntity.status(HttpStatus.OK).body(res.tickets)
    }

    @GetMapping("v1/ticket/{ticketID}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    fun getQRCodeTicket(@PathVariable ticketID: Long,
                        @RequestHeader("Authorization") jwt: String)
    : ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val res = userService.getQRCodeTicket(ticketID)
        return ResponseEntity.status(HttpStatus.OK).body(res.ticket)
    }

    @PostMapping("v1/ticket/generate")
    //@PreAuthorize("hasRole('ROLE_TICKETCATALOGUESERVICE')")
    fun generateTickets(//@RequestHeader("Authorization") jwt: String,
                        @RequestBody reqBody: TicketsToGenerateDTO)
    : ResponseEntity<Any> {
        val res = userService.generateTickets(reqBody)
        return ResponseEntity.status(HttpStatus.OK).body(res)
    }
}