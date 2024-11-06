package it.polito.wa2.group22.authservice.security

import it.polito.wa2.group22.authservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration @EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter() {

    @Value("\${jwt.key}")
    lateinit var key: String

    @Autowired
    lateinit var userDetailsService: UserService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder


    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/login/**").permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/register/**").permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/validate/**").permitAll()
        http.addFilter(JWTAuthenticationFilter(authenticationManagerBean(), key))
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}