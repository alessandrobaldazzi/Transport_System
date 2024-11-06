package it.polito.wa2.group22.authservice.controllers

import it.polito.wa2.group22.authservice.dto.ActivationDTO
import it.polito.wa2.group22.authservice.dto.CredentialsDTO
import it.polito.wa2.group22.authservice.dto.UserAdminDTO
import it.polito.wa2.group22.authservice.dto.UserDTO
import it.polito.wa2.group22.authservice.interfaces.*
import it.polito.wa2.group22.authservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
class RestController {

    @Value("\${http.header.name}")
    lateinit var httpHeaderName: String

    @Value("\${bearer.prefix}")
    lateinit var bearerPrefix: String


    @Autowired
    lateinit var userService: UserService

    @PostMapping("/user/register")
    fun registerUser(@RequestBody reqBody: UserDTO): ResponseEntity<RegisterResponse> {
        val result = userService.userRegister(reqBody)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result)
    }

    @PostMapping("/user/validate")
    fun validateUser(@RequestBody reqBody: ActivationDTO): ResponseEntity<ValidationResponse> {
        val result = userService.userValidate(reqBody)
        return  ResponseEntity.status(HttpStatus.OK).body(result)

    }

    @PostMapping("/user/login")
    fun login(@RequestBody credentials: CredentialsDTO, response: HttpServletResponse): ResponseEntity<String> {
        val token: String? = userService.login(credentials.username, credentials.password)
         response.addHeader(httpHeaderName, bearerPrefix + token)
        return ResponseEntity(token, HttpStatus.OK)

    }
    @PostMapping("/admin")
    @PreAuthorize("hasAnyAuthority('SUPERADMIN')")
    fun adminRegister(@RequestBody admin: UserAdminDTO) : ResponseEntity<ValidationResponse> {
        val admindto = userService.adminRegister(admin);
        return  ResponseEntity(admindto, HttpStatus.OK)

    }
}