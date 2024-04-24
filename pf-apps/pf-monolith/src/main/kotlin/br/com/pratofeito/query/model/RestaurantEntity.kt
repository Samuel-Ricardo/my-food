package br.com.pratofeito.query.model

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.math.BigDecimal
import java.util.Collections
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
    var menu: RestaurantMenuEmbeddable?,
    @OneToMany(mappedBy = "restaurant")
    var orders: List<RestaurantOrderEntity>
)  {
    constructor() : this("", 0, "", null, Collections.emptyList())

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}

@Embeddable
@Access(AccessType.FIELD)
data class RestaurantMenuEmbeddable (
    @ElementCollection
    var menuItems: List<MenuItemEmbeddable>,
    var menuVersion: String,
) {
    constructor() : this(Collections.emptyList(), "")

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}

@Embeddable
@Access(AccessType.FIELD)
data class MenuItemEmbeddable (
    var menuId: String,
    var name: String,
    var price: BigDecimal
)  {
    constructor() : this("", "", BigDecimal(0))

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}