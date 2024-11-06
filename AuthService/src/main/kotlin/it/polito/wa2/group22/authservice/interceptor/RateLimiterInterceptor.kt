package it.polito.wa2.group22.authservice.interceptor

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class RateLimiterInterceptor : HandlerInterceptor{

    private val bucket : Bucket = Bucket
        .builder()
        .addLimit(
            Bandwidth.classic(10,
                Refill.intervally(10, Duration.ofSeconds(1))
            )
        ).build()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return if ( bucket.tryConsume(1) ){
             true
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value())
            false
        }
    }
}