package it.polito.wa2.group22.ticketcatalogservice.converter

import io.r2dbc.spi.Row
import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.entities.User
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class OrderReadingConverter : Converter<Row, Order> {
    override fun convert(source: Row): Order {
        val ticket = Ticket(
            (source.get("id") as Int).toLong(),
            source.get("name") as String,
            (source.get("price") as Double).toFloat(),
            source.get("duration") as Int?,
            source.get("zones") as String,
            source.get("type") as String,
            source.get("max_age") as Int?,
            source.get("min_age") as Int?
        )

        val user = User(
            source.get("email") as String,
            source.get("username") as String
        )

        return Order(
            (source.get("id") as Int).toLong(),
            (source.get("ticketid") as Int).toLong(),
            source.get("quantity") as Int,
            source.get("username") as String,
            source.get("status") as String,
            ticket,
            user
        )
    }
}