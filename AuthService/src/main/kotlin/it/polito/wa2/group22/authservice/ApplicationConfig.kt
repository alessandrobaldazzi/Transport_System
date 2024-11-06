package it.polito.wa2.group22.authservice

import it.polito.wa2.group22.authservice.interceptor.RateLimiterInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApplicationConfig : WebMvcConfigurer{

    @Autowired
    lateinit var interceptor: RateLimiterInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor).addPathPatterns("/user/**")
    }
}