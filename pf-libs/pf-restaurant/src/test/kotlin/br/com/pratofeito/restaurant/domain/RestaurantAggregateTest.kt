package br.com.pratofeito.restaurant.domain

import br.com.pratofeito.common.domain.api.model.AuditEntry
import br.com.pratofeito.common.domain.api.model.Money
import java.util.Calendar
import br.com.pratofeito.restaurant.domain.Restaurant;
import br.com.pratofeito.restaurant.domain.api.CreateRestaurantCommand
import br.com.pratofeito.restaurant.domain.api.CreateRestaurantOrderCommand
import br.com.pratofeito.restaurant.domain.api.RestaurantCreatedEvent
import br.com.pratofeito.restaurant.domain.api.RestaurantOrderCreatedEvent
import br.com.pratofeito.restaurant.domain.api.model.*
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Restaurant>;

    private val who = "just_samuel";
    private var auditEntry = AuditEntry(who, Calendar.getInstance().time);

    private val restaurantName = "Padoca do Samuca"

    private val orderId: RestaurantOrderId = RestaurantOrderId("orderId");
    private val restaurantId: RestaurantId = RestaurantId("restaurantId");

    private val lineItem: RestaurantOrderLineItem = RestaurantOrderLineItem(1, "menuItemId", "itemName");
    private val lineItems: MutableList<RestaurantOrderLineItem> = mutableListOf();

    private val orderDetails: RestaurantOrderDetails = RestaurantOrderDetails(lineItems);

    @BeforeAll
    fun setup() {
        fixture = AggregateTestFixture(Restaurant::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor());

        lineItems.add(lineItem);
    }


    @Test
    fun createRestaurantTest() {

        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("id", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")

        val createRestaurantCommand = CreateRestaurantCommand(restaurantName, menu, auditEntry)
        val restaurantCreatedEvent = RestaurantCreatedEvent(restaurantName, menu, createRestaurantCommand.targetAggregateIdentifier, auditEntry)

        fixture.given().`when`(createRestaurantCommand).expectEvents(restaurantCreatedEvent)
    }

    @Test
    fun createRestaurantOrderTest() {

        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("menuItemId", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")

        val restaurantCreatedEvent = RestaurantCreatedEvent(restaurantName, menu,restaurantId, auditEntry)
        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(restaurantId, orderDetails, orderId, auditEntry)
        val restaurantOrderCreatedEvent = RestaurantOrderCreatedEvent(lineItems, orderId, restaurantId, auditEntry)

        fixture.given(restaurantCreatedEvent)
            .`when`(createRestaurantOrderCommand)
            .expectEvents(restaurantOrderCreatedEvent)
    }

    @Test
    fun createRestaurantOrderFailTest() {

        val menuItems = ArrayList<MenuItem>()
        val item = MenuItem("WRONG", "name", Money(BigDecimal.valueOf(100)))
        menuItems.add(item)
        val menu = RestaurantMenu(menuItems, "v1")

        val restaurantCreatedEvent = RestaurantCreatedEvent(restaurantName, menu,restaurantId, auditEntry)
        val createRestaurantOrderCommand = CreateRestaurantOrderCommand(restaurantId, orderDetails, orderId, auditEntry)

        fixture
            .given(restaurantCreatedEvent)
            .`when`(createRestaurantOrderCommand)
            .expectException(IllegalArgumentException::class.java);
    }
}
