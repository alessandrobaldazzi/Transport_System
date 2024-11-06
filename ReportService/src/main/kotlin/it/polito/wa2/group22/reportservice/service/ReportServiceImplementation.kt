package it.polito.wa2.group22.reportservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.group22.reportservice.dto.GlobalReportDTO
import it.polito.wa2.group22.reportservice.dto.TimePeriodDTO
import it.polito.wa2.group22.reportservice.dto.UserReportDTO
import it.polito.wa2.group22.reportservice.repository.OrderRepository
import it.polito.wa2.group22.reportservice.repository.TransactionRepository
import it.polito.wa2.group22.reportservice.repository.TransitRepository
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ReportServiceImplementation : ReportService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var transitRepository: TransitRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): GlobalReportDTO {
        //Payment Report Controller
        val response: String = WebClient
            .create("http://localhost:8081")
            .post()
            .uri("/admin/payment/report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        return ob.readValue(response, GlobalReportDTO::class.java)
    }

    override suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): UserReportDTO {
        val response: String = WebClient
            .create("http://localhost:8081")
            .post()
            .uri("/admin/payment/report/$username")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        return ob.readValue(response, UserReportDTO::class.java)
    }

    suspend fun getGlobalStats(jwt: String, dataRange: TimePeriodDTO): GlobalReportDTO {
        val transactionsList = transactionRepository.findAll().filter {
            it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.toList()
        val orderList = if (transactionsList.isNotEmpty()) {
            orderRepository.findAll().filter {
                it.id!! >= transactionsList.minOf { it.id!! } &&
                        it.id!! <= transactionsList.maxOf { it.id!! }
            }.toList()
        } else {
            emptyList()
        }
        val transitList = transitRepository.findAll().filter {
            it.transit_date.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.transit_date.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.toList()

        val totalProfits = transactionsList.sumOf { it.amount }.toFloat()
        val totalTickets = orderList.sumOf { it.quantity }
        val ordinaryTicketsCount = orderList.filter { it.ticket_type == "Ordinary" }.sumOf { it.quantity }.toFloat()
        val ordinaryTransitCount = transitList.count { it.ticket_type == "Ordinary" }

        return GlobalReportDTO(
            transactionsList.count(),
            totalProfits,
            transitList.count(),
            if (totalTickets != 0) totalProfits / totalTickets else 0f,
            if (totalTickets != 0) ordinaryTicketsCount / totalTickets * 100 else 0f,
            if (totalTickets != 0) (totalTickets - ordinaryTicketsCount) / totalTickets * 100 else 0f,
            if (transitList.isNotEmpty()) ordinaryTransitCount.toFloat() / transitList.count() * 100 else 0f,
            if (transitList.isNotEmpty()) (transitList.count() - ordinaryTransitCount.toFloat()) / transitList.count() * 100 else 0f,
            totalTickets
        )
    }

    suspend fun getUserStats(jwt: String, dataRange: TimePeriodDTO, username: String): UserReportDTO {

        val transactionsList = transactionRepository.findAll().filter {
            it.username == username &&
                    it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.toList()

        val orderList = if (transactionsList.isNotEmpty()) {
            orderRepository.findAll().filter {
                it.id!! >= transactionsList.minOf { it.id!! } &&
                        it.id!! <= transactionsList.maxOf { it.id!! }
            }.toList()
        } else {
            emptyList()
        }

        val transitList = transitRepository.findAll().filter {
            it.username == username &&
                    it.transit_date.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.transit_date.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.toList()

        val totalProfits = transactionsList.sumOf { it.amount }.toFloat()
        val totalTickets = orderList.sumOf { it.quantity }

        val ordinaryTicketsCount = orderList.filter { it.ticket_type == "Ordinary" }.sumOf { it.quantity }.toFloat()
        val ordinaryTransitCount = transitList.count { it.ticket_type == "Ordinary" }

        return UserReportDTO(
            transactionsList.count(),
            totalProfits,
            transitList.count(),
            if (totalTickets != 0) totalProfits / totalTickets else 0f,
            if (transactionsList.isNotEmpty()) transactionsList.minOf { it.amount }.toFloat() else 0f,
            if (transactionsList.isNotEmpty()) transactionsList.maxOf { it.amount }.toFloat() else 0f,
            if (totalTickets != 0) ordinaryTicketsCount / totalTickets * 100 else 0f,
            if (totalTickets != 0) (totalTickets - ordinaryTicketsCount) / totalTickets * 100 else 0f,
            if (transitList.isNotEmpty()) ordinaryTransitCount.toFloat() / transitList.count() * 100 else 0f,
            if (transitList.isNotEmpty()) (transitList.count() - ordinaryTransitCount.toFloat()) / transitList.count() * 100 else 0f,
            totalTickets
        )
    }
}