package br.com.pratofeito.query.model

import br.com.pratofeito.restaurant.domain.api.model.RestaurantOrderState
import org.axonframework.modelling.command.AggregateVersion
import javax.persistence.*

@Entity
class RestaurantOrderEntity (
    var id: String,
    var aggregateVersion: Long,
    var lineItems: ArrayList<RestaurantOrderItemEmbedable>,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RESTAURANT_ID")
    var resturant: RestaurantEntity,
    @Enumerated var state: RestaurantOrderState,
)

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantOrderItemEmbedable(
    var menuId: String,
    var name: String,
    var quantity: Int,
)