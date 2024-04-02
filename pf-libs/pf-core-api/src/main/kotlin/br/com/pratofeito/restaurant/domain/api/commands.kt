package br.com.pratofeito.restaurant.domain.api

import br.com.pratofeito.common.domain.api.AuditableAbstractCommand
import br.com.pratofeito.common.domain.api.model.AuditEntry
import br.com.pratofeito.restaurant.domain.api.model.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

abstract class RestaurantCommand(
    open val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry,
): AuditableAbstractCommand(auditEntry)

abstract class RestaurantOrderCommand(
    open val aggregateIdentifier: RestaurantOrderId,
    override val auditEntry: AuditEntry,
): AuditableAbstractCommand(auditEntry);

data class CreateRestaurantCommand(

    val name: String,
    @field:Valid val menu: RestaurantMenu,
    @TargetAggregateIdentifier
    override val aggregateIdentifier: RestaurantId,
    override val auditEntry: AuditEntry,

): RestaurantCommand(aggregateIdentifier, auditEntry) {

    constructor(
        name: String, menu: RestaurantMenu, auditEntry: AuditEntry
    ): this(name, menu, RestaurantId(), auditEntry);
}

data class CreateRestaurantOrderCommand(
    @TargetAggregateIdentifier
    override val aggregateIdentifier: RestaurantId,
    @field:Valid val orderDetails: RestaurantOrderDetails,
    val restaurantOrderId: RestaurantOrderId,
    override val auditEntry: AuditEntry,
): RestaurantCommand(aggregateIdentifier, auditEntry) {
    constructor(
        targetAggregateIdentifier: RestaurantId, orderDetails: RestaurantOrderDetails, auditEntry: AuditEntry
    ): this(targetAggregateIdentifier, orderDetails, RestaurantOrderId(), auditEntry)
}

data class MarkRestaurantOrderAsPreparedCommand(
    @TargetAggregateIdentifier
    override val aggregateIdentifier: RestaurantOrderId,
    override val auditEntry: AuditEntry,
): RestaurantOrderCommand(aggregateIdentifier, auditEntry)