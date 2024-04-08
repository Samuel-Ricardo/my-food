package br.com.pratofeito.order.domain.api.model;

import br.com.pratofeito.common.domain.api.model.Money
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.util.UUID
import javax.validation.Valid

data class OrderId(val identifier: String) {

    constructor(): this(UUID.randomUUID().toString())

    override fun toString() = identifier;
}

data class OrderLineItem (
    val menuId: String,
    val name: String,
    @field:Valid val price: Money,
    val quantity: Int
) {
    val total: Money get() = price.multiply(quantity);
}

open class OrderInfo(
    val customerId: String,
    val restaurantId: String,
    @field:Valid val lineItems: List<OrderLineItem>
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}

data class OrderDetails(val orderInfo: OrderInfo, val orderTotal: Money) :
    OrderInfo(orderInfo.customerId, orderInfo.restaurantId, orderInfo.lineItems);

enum class OrderState {
    CREATE_PENDING,
    VERIFIED_BY_CUSTOMER,
    VERIFIED_BY_RESTAURANT,
    PREPARED,
    READY_FOR_DELIVERY,
    DELIVERED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED
}