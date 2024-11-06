package it.polito.wa2.group22.travelerservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationTokenFilter: OncePerRequestFilter() {

    @Autowired
    lateinit var jwtUtils: JwtUtils


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response)
            return
        }
        val token = authHeader.substring("Bearer ".length)
        if (jwtUtils.validateJwt(token)) {
            val userDetails = jwtUtils.getDetailsJwt(token)
            val authenticationToken =
                UsernamePasswordAuthenticationToken(userDetails!!.username, null, userDetails.roles)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken)
        }
        filterChain.doFilter(request, response)
    }
}