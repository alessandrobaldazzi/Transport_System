package it.polito.wa2.group22.authservice.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.authservice.dto.UserDetailsDTO
import it.polito.wa2.group22.authservice.dto.UserLoginDTO
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse



class JWTAuthenticationFilter(authenticationManager: AuthenticationManager, var key: String):
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    init {
        setFilterProcessesUrl("/user/login")
    }

    private val JWT_EXP_TIME = 1000 * 60 * 60  //1H


    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val mapper = jacksonObjectMapper()
            val credentials = mapper.readValue<UserLoginDTO>(request.inputStream)
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    credentials.username,
                    credentials.password,
                    ArrayList()
                )
            )
        } catch (e: IOException) {
            throw AuthenticationServiceException(e.message)
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authentication: Authentication) {
        var user = authentication.principal as UserDetailsDTO

        val token = generateToken(user)
        response?.addHeader("Authorization", "Bearer $token")

    }

     private fun generateToken(user: UserDetailsDTO): String{
        var username = user.username
        var roles = user.authorities.map { it.authority.toString() }
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_EXP_TIME))
            .claim("roles",roles)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }
}