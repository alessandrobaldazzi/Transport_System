package it.polito.wa2.group22.paymentservice.services

import it.polito.wa2.group22.paymentservice.dtos.*
import it.polito.wa2.group22.paymentservice.repositories.PaymentRepository
import kotlinx.coroutines.flow.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.group22.paymentservice.entities.toDTO

@Service
class ReportService {

    @Autowired
    lateinit var transactionRepository: PaymentRepository

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    suspend fun getGlobalReport(dataRange: TimePeriodDTO, jwt: String): GlobalReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.status == 1 &&
                    it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        //Send a request to ticketCatalogue
        //TODO: something is broken, like my soul
        val response: String = WebClient
            .create("http://localhost:8182")
            .post()
            .uri("/admin/ticketcatalog/report")
            .header("Authorization", jwt)
            .bodyValue(transactionList.map { it.orderId }.toList())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        //Send a request to Transit
        val response2: String = WebClient
            .create("http://localhost:8087")
            .post()
            .uri("/admin/transits/report")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        val percentages = ob.readValue(response, PurchasesStatsDTO::class.java)
        val transits = ob.readValue(response2, TransitsStatsDTO::class.java)
        return if (percentages.ticketsNumber == 0){
            GlobalReportDTO(
                transactionList.count(),
                transactionList.toList().sumOf { it.amount }.toFloat(),
                transits.transits,
                -1.0f,
                percentages.percOrdinaryTickets.toFloat(),
                percentages.percTravelerCards.toFloat(),
                transits.percOrdinaryTransits,
                transits.percTravelerCardsTransits,
                0
            )
        }
        else{
            GlobalReportDTO(
                transactionList.count(),
                transactionList.toList().sumOf { it.amount }.toFloat(),
                transits.transits,
                transactionList.toList().sumOf { it.amount }.toFloat() / percentages.ticketsNumber,
                percentages.percOrdinaryTickets.toFloat(),
                percentages.percTravelerCards.toFloat(),
                transits.percOrdinaryTransits,
                transits.percTravelerCardsTransits,
                percentages.ticketsNumber
            )
        }
    }

    suspend fun getUserReport(dataRange: TimePeriodDTO, username: String, jwt: String): UserReportDTO {
        val transactionList = transactionRepository.findAll().filter {
            it.userId == username &&
                    it.status == 1 &&
                    it.issuedAt.isAfter(LocalDateTime.parse(dataRange.start_date, formatter)) &&
                    it.issuedAt.isBefore(LocalDateTime.parse(dataRange.end_date, formatter))
        }.map { it.toDTO() }
        val response: String = WebClient
            .create("http://localhost:8182")
            .post()
            .uri("/admin/ticketcatalog/report")
            .header("Authorization", jwt)
            .bodyValue(transactionList.map { it.orderId }.toList())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val response2: String = WebClient
            .create("http://localhost:8087")
            .post()
            .uri("/admin/transits/report/$username")
            .header("Authorization", jwt)
            .bodyValue(dataRange)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
        val ob = jacksonObjectMapper()
        val percentages = ob.readValue(response, PurchasesStatsDTO::class.java)
        val transits = ob.readValue(response2, TransitsStatsDTO::class.java)
        return if(percentages.ticketsNumber != 0) {
            UserReportDTO(
                transactionList.count(),
                transactionList.toList().sumOf { it.amount }.toFloat(),
                transits.transits,
                transactionList.toList().sumOf { it.amount }.toFloat() / percentages.ticketsNumber,
                transactionList.toList().minOf { it.amount }.toFloat(),
                transactionList.toList().maxOf { it.amount }.toFloat(),
                percentages.percOrdinaryTickets.toFloat(),
                percentages.percTravelerCards.toFloat(),
                transits.percOrdinaryTransits,
                transits.percTravelerCardsTransits,
                percentages.ticketsNumber
            )
        }
        else{
            UserReportDTO(
                transactionList.count(),
                transactionList.toList().sumOf { it.amount }.toFloat(),
                transits.transits,
                -1.0f,
                -1.0f,
                -1.0f,
                percentages.percOrdinaryTickets.toFloat(),
                percentages.percTravelerCards.toFloat(),
                transits.percOrdinaryTransits,
                transits.percTravelerCardsTransits,
                0
            )
        }
    }
}