package it.polito.wa2.group22.ticketcatalogservice.repositories

import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : CoroutineCrudRepository<Order, Long> {

    @Query(
        """
       SELECT * 
       FROM orders o, tickets t, users u 
       WHERE o.username = u.username 
       AND o.ticketid = t.id 
    """
    )
    fun findAllOrders(): Flow<Order>

    @Query(
        """
        SELECT * 
       FROM orders o, tickets t, users u 
       WHERE o.username = u.username 
       AND o.ticketid = t.id 
       AND o.id = :id AND o.username = :username
    """
    )
    suspend fun findOrderByUsername(@Param("id") id: Long, @Param("username") username: String): Order?

    @Query(
        """
        SELECT * 
       FROM orders o, tickets t, users u 
       WHERE o.username = u.username 
       AND o.ticketid = t.id 
       AND o.id = :id
    """
    )
    suspend fun findOrderById(@Param("id") id: Long): Order?

    @Query(
        """
         SELECT *
         FROM orders o, tickets t, users u 
         WHERE o.username = u.username
         AND o.ticketid = t.id 
         AND o.username = :username
     """
    )
    fun findOrdersByUser(@Param("username") username: String): Flow<Order>
}