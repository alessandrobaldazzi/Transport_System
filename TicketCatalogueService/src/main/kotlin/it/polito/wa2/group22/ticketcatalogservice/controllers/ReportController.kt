package it.polito.wa2.group22.ticketcatalogservice.controllers

import it.polito.wa2.group22.ticketcatalogservice.dtos.PurchaseStatsDTO
import it.polito.wa2.group22.ticketcatalogservice.services.ReportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

// These are the controllers called by the report service
// to generate the company report and single travelers reports
@RestController
@RequestMapping("/admin/ticketcatalog")
class ReportController {

    @Autowired
    lateinit var reportService: ReportService
    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    suspend fun report(
        @RequestBody orderIDs: List<Int>,
        @RequestHeader("Authorization") authorizationHeader: String,
    ): PurchaseStatsDTO {
        return reportService.getOrdersInfo(orderIDs, authorizationHeader)
    }
}