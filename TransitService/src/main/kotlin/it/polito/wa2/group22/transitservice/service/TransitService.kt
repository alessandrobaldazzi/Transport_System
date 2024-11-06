package it.polito.wa2.group22.transitservice.service

import it.polito.wa2.group22.transitservice.dto.TimePeriodDTO
import it.polito.wa2.group22.transitservice.dto.TicketDTO
import it.polito.wa2.group22.transitservice.dto.TransitDTO
import it.polito.wa2.group22.transitservice.dto.StatisticsDTO
import kotlinx.coroutines.flow.Flow

interface TransitService {
    suspend fun insertNewTransit(ticket : TicketDTO) : TransitDTO
    fun getAllTransits(): Flow<TransitDTO>
    suspend fun getRepTransits(datarange: TimePeriodDTO): StatisticsDTO
    suspend fun getUserRepTransits(datarange: TimePeriodDTO, username: String): StatisticsDTO
}