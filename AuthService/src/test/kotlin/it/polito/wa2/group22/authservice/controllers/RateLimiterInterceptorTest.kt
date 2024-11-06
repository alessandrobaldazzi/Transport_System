package it.polito.wa2.group22.authservice.controllers

import it.polito.wa2.group22.authservice.dto.UserDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RateLimiterInterceptorTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun sleepingTime(){
        Thread.sleep(2000)
    }

    @Test
    fun tenRequestsAfterOneSecondTest(){
        val baseUrl = "http://localhost:$port"
        val responseResults = mutableListOf<HttpStatus>()
        var result = false

        val request = HttpEntity(UserDTO(null, "userValidateInvalidActivationTest", "Secret!Password1", "userValidateInvalidActivationTest@gmail.com"))


        for(i in 1..21){
            val response = restTemplate.postForEntity<String>(
                "$baseUrl/user/register",
                request,
            )
            responseResults.add(response.statusCode)
        }
        for(i in responseResults){
            if(!result && i == HttpStatus.TOO_MANY_REQUESTS)
                result = true
        }
        Assertions.assertTrue(result)
        Thread.sleep(1000)
         val response2 = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request
        )
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.statusCode)
    }

    @Test
    fun tooManyRequestInvalidTest(){
        val baseUrl = "http://localhost:$port"
        val responseResults = mutableListOf<HttpStatus>()
        var result = false
        val request = HttpEntity(UserDTO(null, "userValidateInvalidActivationTest", "Secret!Password1", "userValidateInvalidActivationTest@gmail.com"))
        for(i in 1..21){
            val response = restTemplate.postForEntity<String>(
                "$baseUrl/user/register",
                request,
            )
            responseResults.add(response.statusCode)
        }
        for(i in responseResults){
            if(!result && i == HttpStatus.TOO_MANY_REQUESTS)
                result = true
        }

        Assertions.assertTrue(result)

    }
}