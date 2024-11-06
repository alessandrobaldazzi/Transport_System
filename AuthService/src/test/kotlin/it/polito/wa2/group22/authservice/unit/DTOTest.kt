package it.polito.wa2.group22.authservice.unit

import it.polito.wa2.group22.authservice.dto.ActivationDTO
import it.polito.wa2.group22.authservice.dto.UserDTO
import it.polito.wa2.group22.authservice.dto.toDTO
import it.polito.wa2.group22.authservice.entities.Activation
import it.polito.wa2.group22.authservice.entities.User
import it.polito.wa2.group22.authservice.repositories.ActivationRepository
import it.polito.wa2.group22.authservice.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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
internal class DTOTest {

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
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var activationRepo: ActivationRepository

    @Test
    fun activationDTOValid() {
        var user: User = User("username", "password", "email@email.com")
        val savedUser = userRepo.save(user)
        var code: String = "ActivationCodeVerifier";
        var activation: Activation = Activation(savedUser, code)
        val res = activationRepo.save(activation)
        var actDTO: ActivationDTO = ActivationDTO("email@email.com", res.activationCode, res.id!!, Date())
        Assertions.assertEquals(actDTO.email, res.toDTO().email)
        Assertions.assertEquals(actDTO.provisional_id, res.toDTO().provisional_id)

    }

    @Test
    fun userDTOValid() {
        val user = User("test", "pass", "email@test.it")
        val savedUser = userRepo.save(user)
        Assertions.assertEquals(savedUser.toDTO(), UserDTO(savedUser.id, "test", "pass", "email@test.it"))
    }
}