package br.com.pratofeito.query.handler

import br.com.pratofeito.query.model.RestaurantOrderEntity
import br.com.pratofeito.query.model.RestaurantOrderItemEmbedable
import br.com.pratofeito.query.repository.RestaurantOrderRepository
import br.com.pratofeito.query.repository.RestaurantRepository
import br.com.pratofeito.restaurant.domain.api.RestaurantOrderCreatedEvent
import br.com.pratofeito.restaurant.domain.api.RestaurantOrderPreparedEvent
import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderState
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurantorder")
class RestaurantOrderHandler (
    private val repository: RestaurantOrderRepository,
    private val restaurantRepository: RestaurantRepository,
    private val messagingTemplate: SimpMessageSendingOperations
) {

    @EventHandler
    @AllowReplay(true)
    fun handle(event: RestaurantOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {

        val restaurantOrderItems = ArrayList<RestaurantOrderItemEmbedable>()

        for (item in event.lineItems) {

            val restaurantOrderItem = RestaurantOrderItemEmbedable(item.menuItemId, item.name, item.quantity)
            restaurantOrderItems.add(restaurantOrderItem)

        }

        val restaurantEntity = restaurantRepository.findById(event.aggregateIdentifier.identifier).get()
        val restaurantOrderEntiy = RestaurantOrderEntity(
            event.restaurantOrderId.identifier,
            aggregateVersion, restaurantOrderItems,
            restaurantEntity,
            RestaurantOrderState.CREATED
        )

        repository.save(restaurantOrderEntiy)
        broadcastUpdate()

    }

    @EventHandler
    @AllowReplay(true)
    fun handle(event: RestaurantOrderPreparedEvent, @SequenceNumber aggregateVersion: Long) {

        val restaurantOrder = repository.findById(event.aggregateIdentifier.identifier).get();

        restaurantOrder.state = RestaurantOrderState.PREPARED
        repository.save(restaurantOrder)

        broadcastUpdate();
    }

    @ResetHandler
    fun onReset() = repository.deleteAll()
    private fun broadcastUpdate() = messagingTemplate.convertAndSend("/topic/restaurants/orders.updates", repository.findAll())

}