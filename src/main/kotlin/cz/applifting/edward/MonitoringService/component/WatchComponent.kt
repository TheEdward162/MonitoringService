package cz.applifting.edward.MonitoringService

import java.util.concurrent.ScheduledFuture

import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.Async
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.transaction.event.TransactionalEventListener

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger

import org.springframework.beans.factory.annotation.Autowired

@Component
class WatchComponent(
	@Autowired private val endpointRepository: EndpointRepository,
	@Autowired private val resultRepository: ResultRepository,
	@Autowired private val scheduler: ThreadPoolTaskScheduler
) {
	private val tasks: MutableMap<Int, ScheduledFuture<*>> = mutableMapOf()

	private fun startTask(endpoint: MonitoredEndpoint) {
		println("Registering endpoint " + endpoint.url + " at interval " + endpoint.monitoredInterval)
		val future = scheduler.schedule(
			WatchTask(endpoint, this.endpointRepository, this.resultRepository),
			PeriodicTrigger(endpoint.monitoredInterval * 1000)
		)
		this.tasks.put(endpoint.id, future!!)
	}

	@EventListener
    fun handleContextStart(event: ContextStartedEvent) {
		this.endpointRepository.findAll().forEach({
			endpoint ->
			this.startTask(endpoint)
		})
    }

	@TransactionalEventListener
	fun handleEndpointTransaction(event: EndpointChangedEvent) {
		val endpoint = this.endpointRepository.findEndpointById(event.endpoint_id)

		val task = this.tasks.get(event.endpoint_id)
		if (task != null) {
			task.cancel(true)
		}
		if (endpoint != null) {
			this.startTask(endpoint)
		}
	}
}