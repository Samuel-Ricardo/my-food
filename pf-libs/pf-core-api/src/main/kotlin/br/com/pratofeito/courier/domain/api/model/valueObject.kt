package br.com.pratofeito.courier.domain.api.model

import java.util.UUID


enum class CourierOrderState {
    CREATED, ASSIGN_PENDING, ASSIGNED, DELIVERED, CANCEL_PENDING, CANCELED
}

data class CourierId(val identifier: String) {

    constructor(): this(UUID.randomUUID().toString());

    override fun toString(): String = identifier;
}

data class CourierOrderId(val identifier: String) {

    constructor(): this(UUID.randomUUID().toString());

    override fun toString(): String = identifier;
}