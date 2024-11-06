package it.polito.wa2.group22.transitservice.controller

import it.polito.wa2.group22.transitservice.dto.TimePeriodDTO
import it.polito.wa2.group22.transitservice.dto.TicketDTO
import it.polito.wa2.group22.transitservice.dto.TransitDTO
import it.polito.wa2.group22.transitservice.dto.StatisticsDTO
import it.polito.wa2.group22.transitservice.service.TransitServiceImplementation
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class TransitController(val transitService: TransitServiceImplementation){

    @GetMapping("/admin/transits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    fun getAllTransit() : Flow<TransitDTO> {
        return  transitService.getAllTransits()
    }

    @PostMapping("/transits")
    @PreAuthorize("hasAuthority('MACHINE')")
    suspend fun insertNewTransit(@RequestBody body : TicketDTO) : TransitDTO {
        return transitService.insertNewTransit(body)
    }

    @PostMapping("/admin/transits/report")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun getReportTransit(
        @RequestBody dataRange: TimePeriodDTO
    ) : StatisticsDTO {
        return  transitService.getRepTransits(dataRange)
    }

    @PostMapping("/admin/transits/report/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    suspend fun getUserReportTransit(
        @RequestBody dataRange: TimePeriodDTO,
        @PathVariable username: String,
    ) : StatisticsDTO {
        return  transitService.getUserRepTransits(dataRange, username)
    }





}