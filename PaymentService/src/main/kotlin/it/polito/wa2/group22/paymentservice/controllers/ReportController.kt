package it.polito.wa2.group22.paymentservice.controllers

import it.polito.wa2.group22.paymentservice.dtos.GlobalReportDTO
import it.polito.wa2.group22.paymentservice.dtos.TimePeriodDTO
import it.polito.wa2.group22.paymentservice.dtos.UserReportDTO
import it.polito.wa2.group22.paymentservice.services.ReportService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/payment")
class ReportController(val reportService: ReportService) {

    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun globalReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody dataRange: TimePeriodDTO
    ): GlobalReportDTO {
        return reportService.getGlobalReport(dataRange, authorizationHeader)
    }

    @PostMapping("/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    suspend fun userReport(
        @RequestHeader("Authorization") authorizationHeader: String,
        @PathVariable username: String,
        @RequestBody dataRange: TimePeriodDTO
    ): UserReportDTO {
        return reportService.getUserReport(dataRange, username, authorizationHeader)
    }
}
