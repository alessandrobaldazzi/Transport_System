package it.polito.wa2.group22.travelerservice.controllers

import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetProfileResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelerTicketResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelersResponse
import it.polito.wa2.group22.travelerservice.services.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController {


    @Autowired
    lateinit var adminService: AdminService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("v1/admin/travelers")
    fun getTravelers(@RequestHeader("Authorization") jwt:String)
    :ResponseEntity<Any>{
        val res = adminService.getTravelers()
        return ResponseEntity.status(HttpStatus.OK).body(res.users)
        }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("v1/admin/traveler/{username}/profile")
    fun getTravelerProfile(@PathVariable username:String,
                           @RequestHeader("Authorization") jwt:String)
    :ResponseEntity<Any>{
        val res = adminService.getTravelerProfile(username)
        return ResponseEntity.status(HttpStatus.OK).body(res.user)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("v1/admin/traveler/{username}/tickets")
    fun getTravelerTickets(@PathVariable username:String,
                           @RequestHeader("Authorization") jwt:String)
    :ResponseEntity<Any>{
        val res = adminService.getTravelerTickets(username)
        return ResponseEntity.status(HttpStatus.OK).body(res.tickets)
    }
}