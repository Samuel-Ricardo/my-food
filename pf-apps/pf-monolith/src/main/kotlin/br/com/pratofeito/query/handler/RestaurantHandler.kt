package br.com.pratofeito.query.handler

import br.com.pratofeito.query.model.MenuItemEmbeddable
import br.com.pratofeito.query.model.RestaurantEntity
import br.com.pratofeito.query.model.RestaurantMenuEmbeddable
import br.com.pratofeito.query.repository.RestaurantRepository
import br.com.pratofeito.restaurant.domain.api.RestaurantCreatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurant")
class RestaurantHandler (
    private val repository: RestaurantRepository,
    private val messagingTemplate: SimpMessageSendingOperations
) {
    @EventHandler
    @AllowReplay(true)
    fun handle(event: RestaurantCreatedEvent, @SequenceNumber aggregateVersion: Long) {

        val menuItems = ArrayList<MenuItemEmbeddable>();
        for (item in event.menu.menuItems) {
            val menuItem = MenuItemEmbeddable(item.id, item.name, item.price.amount);
            menuItems.add(menuItem);
        }

        val menu = RestaurantMenuEmbeddable(menuItems, event.menu.menuVersion);

        repository.save(RestaurantEntity(
            event.aggregateIdentifier.identifier,
            aggregateVersion,
            event.name,
            menu,
            emptyList()
        ));

        broadcastUpdate();
    }

    @ResetHandler
    fun onReset() = repository.deleteAll();

    private fun broadcastUpdate() = messagingTemplate.convertAndSend("/topic/restaurants.updates", repository.findAll());
}
