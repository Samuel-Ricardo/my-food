package br.com.pratofeito.web

import br.com.pratofeito.admin.AxonAdministration
import org.axonframework.eventhandling.EventProcessor
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.util.Optional

@Controller
class AdminController (
    private val axonAdministration: AxonAdministration
) {

    @SubscribeMapping("/eventprocessors")
    fun getEventProcessors(): Iterable<EventProcessor> =
        axonAdministration.getTrackingEventProcessor();

    @SubscribeMapping("/eventprocessors/{groupName}")
    fun getEventProcessors(groupName: String): Optional<EventProcessor> =
        axonAdministration.getEventProcessors(groupName);

    @MessageMapping(value = ["/eventprocessors/{groupName}/reply"])
    fun replyEventProcessors(@DestinationVariable groupName: String): ResponseEntity<Any> {
        axonAdministration.resetTrackingEventProcessor(groupName);
        return ResponseEntity.accepted().build();
    }
}