package it.polito.wa2.group22.transitservice.service

import it.polito.wa2.group22.transitservice.repository.TransitRepository
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
import java.math.BigDecimal
import java.time.LocalDateTime


@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransitServiceImplementationTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            /*registry.add("spring.r2dbc.url") {
                "r2dbc:pool:postgresql://"+ postgres::getHost+":"+postgres::getFirstMappedPort+"/"+ postgres::getDatabaseName
            }
            registry.add("spring.r2dbc.username", postgres::getUsername);
            registry.add("spring.r2dbc.password", postgres::getPassword);*/
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var transitService : TransitServiceImplementation

    @Autowired
    lateinit var transitRepository: TransitRepository

    @Test
    fun validGetRepTransits(){

    }

    @Test
    fun invalidTimePeriodGetRepTransits(){

    }

    @Test
    fun validGetUserRepTransits(){

    }

    @Test
    fun invalidTimePeriodGetUserRepTransits(){

    }

    @Test
    fun invalidUsernameGetUserRepTransits(){

    }

    @Test
    fun validInsertNewTransit(){
        var jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWNoaW5lIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIk1BQ0hJTkUiXX0.0tpQOcFv0OCn0ja5x_X_jQ-UUFQeTV-SNrt1Bdt4tAg"

    }

    @Test
    fun invalidTicketDTOInsertNewTransit(){

    }
}