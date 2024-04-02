package br.com.pratofeito.admin

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class AxonAdministration(
    private val eventProcessingConfiguration: EventProcessingConfiguration
) {

    fun resetTrackingEventProcessor(processingGroup:String) = eventProcessingConfiguration
        .eventProcessorByProcessingGroup(processingGroup, TrackingEventProcessor::class.java)
        .ifPresent{
            it.shutDown();
            it.resetTokens();
            it.start();
        }

    fun getTrackingEventProcessor(): List<EventProcessor> = eventProcessingConfiguration
        .eventProcessors()
        .values.filterIsInstance(TrackingEventProcessor::class.java)

    fun getEventProcessors(processingGroup: String): Optional<EventProcessor> =
        eventProcessingConfiguration
            .eventProcessorByProcessingGroup(processingGroup)

}