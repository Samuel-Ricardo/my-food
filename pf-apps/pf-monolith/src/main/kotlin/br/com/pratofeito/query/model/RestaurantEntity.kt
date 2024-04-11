package br.com.pratofeito.query.model

import java.math.BigDecimal
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class RestaurantEntity (
    @Id var id: String,
    var aggregateVersion: Long,
    var name: String,
    @Embedded
    var menu: RestaurantMenuEmbeddable,
    @OneToMany(mappedBy = "restaurant")
    var orders: List<RestaurantOrderEntity>
)

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantMenuEmbeddable (
    @ElementCollection
    var menuItems: List<MenuItemEmbeddable>,
    var menuVersion: String,
)

@Embeddable
@Access(AccessType.FIELD)
data class MenuItemEmbeddable (
    var menuId: String,
    var name: String,
    var price: BigDecimal
)