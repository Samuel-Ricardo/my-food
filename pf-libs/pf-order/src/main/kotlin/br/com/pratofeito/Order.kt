import br.com.pratofeito.common.domain.api.model.Money
import br.com.pratofeito.customer.domain.api.model.CustomerId
import br.com.pratofeito.model.*
import br.com.pratofeito.model.MarkOrderAsPreparedInternalCommand
import br.com.pratofeito.model.MarkOrderAsReadyForDeliveryInternalCommand
import br.com.pratofeito.model.MarkOrderAsVerifiedByCustomerInternalCommand
import br.com.pratofeito.model.MarkOrderAsVerifiedByRestaurantInternalCommand
import br.com.pratofeito.order.domain.api.*
import br.com.pratofeito.order.domain.api.model.OrderDetails
import br.com.pratofeito.order.domain.api.model.OrderId
import br.com.pratofeito.order.domain.api.model.OrderLineItem
import br.com.pratofeito.order.domain.api.model.OrderState
import br.com.pratofeito.restaurant.domain.api.model.MenuItem
import br.com.pratofeito.restaurant.domain.api.model.RestaurantId
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal

@Aggregate
internal class Order{

    private lateinit var id: OrderId;

    private lateinit var lineItems: List<OrderLineItem>;
    private lateinit var restaurantId: RestaurantId;
    private lateinit var customerId: CustomerId;
    private lateinit var state: OrderState;

    val orderTotal: Money get() = calculateOrderTotal(lineItems);

    constructor();

    @CommandHandler
    constructor ( command: CreatedOrderCommand ) {
        AggregateLifecycle.apply(OrderCreationInitiatedEvent(
                OrderDetails(command.orderInfo, calculateOrderTotal(command.orderInfo.lineItems)),
                command.targetAggregateIdentifier,
                command.auditEntry
            )
        )
    }

    private fun calculateOrderTotal(lineItems: List<OrderLineItem>): Money =
        lineItems.stream().map(OrderLineItem::total).reduce(Money(BigDecimal.ZERO), Money::add)

    @EventSourcingHandler
    fun on(event: OrderCreationInitiatedEvent) {
        id = event.aggregateIdentifier;
        customerId = CustomerId(event.orderDetails.customerId);
        restaurantId = RestaurantId(event.orderDetails.restaurantId);
        state = OrderState.CREATE_PENDING;
    }

    @CommandHandler
    fun markOrderAsVerifiedByCustomer(command: MarkOrderAsVerifiedByCustomerInternalCommand) {
        AggregateLifecycle.apply(OrderVerifiedByCustomerEvent(
                command.targetAggregateIdentifier,
                command.customerId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: OrderVerifiedByCustomerEvent){
        state = OrderState.VERIFIED_BY_CUSTOMER
    }

    @CommandHandler
    fun markOrderAsVerifiedByRestaurant(command: MarkOrderAsVerifiedByRestaurantInternalCommand) {
        if (OrderState.VERIFIED_BY_CUSTOMER == state) {
            AggregateLifecycle.apply(OrderVerifiedByRestaurantEvent(
                    command.targetAggregateIdentifier,
                    command.restaurantId,
                    command.auditEntry,
                )
            )
        } else {
            throw UnsupportedOperationException(" Order must be verified by customer before being verified by restaurant")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderVerifiedByRestaurantEvent) {
        state = OrderState.VERIFIED_BY_RESTAURANT
    }

    @CommandHandler
    fun markOrderAsPrepared(command: MarkOrderAsPreparedInternalCommand) {
        if(OrderState.VERIFIED_BY_RESTAURANT == state) {
            AggregateLifecycle.apply(OrderPreparedEvent(
                command.targetAggregateIdentifier, command.auditEntry
            ))
        } else {
            throw UnsupportedOperationException(" Order must be verified by restaurant before being prepared")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderPreparedEvent) {
        state = OrderState.DELIVERED
    }

    @CommandHandler
    fun markOrderAsReadyForDelivery(command: MarkOrderAsReadyForDeliveryInternalCommand) {
        if(OrderState.PREPARED == state) {
            AggregateLifecycle.apply(OrderReadyForDeliveryEvent(
                command.targetAggregateIdentifier,
                command.auditEntry
            ))
        } else {
            throw UnsupportedOperationException(" Order must be prepared before being delivered")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderReadyForDeliveryEvent) {
        state = OrderState.READY_FOR_DELIVERY
    }

    @CommandHandler
    fun markOrderAsDelivery(command: MarkOrderAsDeliveredInternalCommand) {
        if ( OrderState.READY_FOR_DELIVERY == state ) {
            AggregateLifecycle.apply(OrderDeliveredEvent(
                command.targetAggregateIdentifier,
                command.auditEntry
            ))
        } else {
            throw UnsupportedOperationException("Order must be ready for delivery before being delivered")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderDeliveredEvent) {
        state = OrderState.DELIVERED
    }

    @CommandHandler
    fun markOrderAsRejected(command: MarkOrderAsRejectedInternalCommand) {
        if(OrderState.VERIFIED_BY_CUSTOMER == state || OrderState.CREATE_PENDING == state) {
            AggregateLifecycle.apply(OrderRejectedEvent(
                command.targetAggregateIdentifier, command.auditEntry
            ))
        } else {
            throw UnsupportedOperationException(" Order must be verified by customer or create pending before being rejected")
        }
    }

    @EventSourcingHandler
    fun on(event: OrderRejectedEvent) {
        state = OrderState.REJECTED
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}