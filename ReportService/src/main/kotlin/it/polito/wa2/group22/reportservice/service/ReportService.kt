package it.polito.wa2.group22.reportservice.service

import it.polito.wa2.group22.reportservice.dto.GlobalReportDTO
import it.polito.wa2.group22.reportservice.dto.TimePeriodDTO
import it.polito.wa2.group22.reportservice.dto.UserReportDTO

interface ReportService {
    suspend fun getGlobalReport(jwt: String, dataRange: TimePeriodDTO): GlobalReportDTO
    suspend fun getUserReport(jwt: String, dataRange: TimePeriodDTO, username: String): UserReportDTO
}