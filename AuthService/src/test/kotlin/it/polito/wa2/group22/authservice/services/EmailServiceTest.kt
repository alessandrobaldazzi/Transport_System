package it.polito.wa2.group22.authservice.services

import it.polito.wa2.group22.authservice.exceptions.EmailServiceException
import it.polito.wa2.group22.authservice.utils.EmailResult
import it.polito.wa2.group22.authservice.utils.emailResultToMessage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailServiceTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var es: EmailService

    @Test
    fun sendEmailSuccess() {
        val actCode = "RANDOM_ACT_CODE"
        Assertions.assertDoesNotThrow {
            es.sendMail("group22.wa2@libero.it", "pablo", actCode, Date())
        }
    }

    @Test
    fun sendEmailEmptyEmailFailure() {
        //val user: User = User("pablo", "password", "")
        val actCode = "RANDOM_ACT_CODE"
        val exp =  es.sendMail("", "pablo", actCode, Date())
        Assertions.assertEquals(
           EmailResult.MISSING_EMAIL,
            exp
        )
    }

    @Test
    fun sendEmailEmptyUsernameFailure() {
        //val user: User = User("", "password", "group22wa2@gmail.com")
        val actCode = "RANDOM_ACT_CODE"
        val exp = es.sendMail("group22.wa2@libero.it", "", actCode, Date())
        Assertions.assertEquals(
          EmailResult.MISSING_USERNAME,
            exp
        )
    }

    @Test
    fun sendEmailEmptyActivationCodeFailure() {
        //val user: User = User("pablo", "password", "group22wa2@gmail.com")
        val actCode = ""

        val exp =  es.sendMail("group22.wa2@libero.it", "pablo", actCode, Date())
        Assertions.assertEquals(
           EmailResult.MISSING_ACT_CODE,
            exp
        )
    }
}