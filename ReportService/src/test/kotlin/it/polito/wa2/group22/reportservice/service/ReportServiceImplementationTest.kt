package it.polito.wa2.group22.reportservice.service

import it.polito.wa2.group22.reportservice.dto.GlobalReportDTO
import it.polito.wa2.group22.reportservice.dto.TimePeriodDTO
import it.polito.wa2.group22.reportservice.dto.UserReportDTO
import it.polito.wa2.group22.reportservice.entity.Order
import it.polito.wa2.group22.reportservice.entity.Transaction
import it.polito.wa2.group22.reportservice.entity.Transit
import it.polito.wa2.group22.reportservice.repository.OrderRepository
import it.polito.wa2.group22.reportservice.repository.TransactionRepository
import it.polito.wa2.group22.reportservice.repository.TransitRepository
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
class ReportServiceImplementationTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            /*registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)*/
            registry.add("spring.r2dbc.url") {
                "r2dbc:pool:postgresql://"+ postgres::getHost+":"+postgres::getFirstMappedPort+"/"+ postgres::getDatabaseName
            }
            registry.add("spring.r2dbc.username", postgres::getUsername);
            registry.add("spring.r2dbc.password", postgres::getPassword);
            //registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var reportService : ReportServiceImplementation

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var transitRepository: TransitRepository

    @Test
    fun validGetGlobalReport(){

    }

    /*@Test
    fun invalidJWTGetGlobalReport(){


    }

    @Test
    fun invalidTimePeriodGetGlobalReport(){

    }

    @Test
    fun validGetUserReport(){

    }

    @Test
    fun invalidJWTGetUserReport(){

    }

    @Test
    fun invalidTimePeriodGetUserReport(){

    }

    @Test
    fun invalidUsernameGetUserReport(){

    }*/

    @Test
    suspend fun validGetGlobalStats(){
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIkFETUlOIiwiU1VQRVJBRE1JTiJdfQ.dhHy89_qpdq-k5ZAxzsY7qZoq5knUZ7FsG8PTnjx6Qc"
        val dataRange = TimePeriodDTO("2022-04-12 12:00:00", "2022-12-14 18:00:00")
        val globalStats = reportService.getGlobalStats(jwt, dataRange)
        Assertions.assertEquals(globalStats, GlobalReportDTO(4, 130.0f, 2, 16.25f, 50.0f, 50.0f, 0.0f, 100.0f, 8) )
    }

    @Test
    suspend fun invalidTimePeriodGetGlobalStats(){
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIkFETUlOIiwiU1VQRVJBRE1JTiJdfQ.dhHy89_qpdq-k5ZAxzsY7qZoq5knUZ7FsG8PTnjx6Qc"
        val dataRange = TimePeriodDTO("2022-04-12 12:00:00", "2021-12-14 18:00:00")
        val globalStats = reportService.getGlobalStats(jwt, dataRange)
        Assertions.assertEquals(globalStats, GlobalReportDTO(0, 0.0f, 0, 0f, 0.0f, 0.0f, 0.0f, 0.0f, 0) )
    }

    @Test
    suspend fun validGetUserStats(){
        orderRepository.save(Order("Ordinary", 2, "customer"))
        transactionRepository.save(Transaction(BigDecimal(50), "customer", LocalDateTime.now()))
        transitRepository.save(Transit("Monthly", "customer", LocalDateTime.now()))
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIkFETUlOIiwiU1VQRVJBRE1JTiJdfQ.dhHy89_qpdq-k5ZAxzsY7qZoq5knUZ7FsG8PTnjx6Qc"
        val dataRange = TimePeriodDTO("2022-04-12 12:00:00", "2025-12-14 18:00:00")
        val userStats = reportService.getUserStats(jwt, dataRange, "customer")
        Assertions.assertEquals(userStats, UserReportDTO(1, 50.0f, 1, 25.0f, 50.0f, 50.0f, 100.0f, 0.0f, 0.0f, 100.0f, 2))
    }

    @Test
    suspend fun invalidTimePeriodGetUserStats(){
        orderRepository.save(Order("Ordinary", 2, "customer"))
        transactionRepository.save(Transaction(BigDecimal(50), "customer", LocalDateTime.now()))
        transitRepository.save(Transit("Monthly", "customer", LocalDateTime.now()))
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIkFETUlOIiwiU1VQRVJBRE1JTiJdfQ.dhHy89_qpdq-k5ZAxzsY7qZoq5knUZ7FsG8PTnjx6Qc"
        val dataRange = TimePeriodDTO("2022-04-12 12:00:00", "2021-12-14 18:00:00")
        val userStats = reportService.getUserStats(jwt, dataRange, "customer")
        Assertions.assertEquals(userStats, UserReportDTO(0, 0.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0))
    }

    @Test
    suspend fun invalidUsernameGetUserStats(){
        orderRepository.save(Order("Ordinary", 2, "customer"))
        transactionRepository.save(Transaction(BigDecimal(50), "customer", LocalDateTime.now()))
        transitRepository.save(Transit("Monthly", "customer", LocalDateTime.now()))
        val jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiaWF0IjoxNjY2MTcxODc2LCJleHAiOjIwNjYyMTU0NzYsInJvbGVzIjpbIkFETUlOIiwiU1VQRVJBRE1JTiJdfQ.dhHy89_qpdq-k5ZAxzsY7qZoq5knUZ7FsG8PTnjx6Qc"
        val dataRange = TimePeriodDTO("2022-04-12 12:00:00", "2025-12-14 18:00:00")
        val userStats = reportService.getUserStats(jwt, dataRange, "gennaro")
        Assertions.assertEquals(userStats, UserReportDTO(0, 0.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0))
    }


}