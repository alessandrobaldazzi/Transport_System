package it.polito.wa2.group22.transitservice.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtAuthFilter(private val jwtParser: JwtUtils) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val token = exchange.request.headers.getFirst("Authorization")?.trim()?.split(" ")?.get(1)

        if (token != null && jwtParser.validateJwt(token)) {
            val user = jwtParser.getDetailsJwt(token)
            val authenticatedUser =
                UsernamePasswordAuthenticationToken(user.userName, null, user.roles)

            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticatedUser))
        }

        return chain.filter(exchange)
    }
}