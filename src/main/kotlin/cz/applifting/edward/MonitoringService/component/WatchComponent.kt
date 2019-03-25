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

	/// Starts a task and saves it into the hashmap.
	private fun startTask(endpoint: MonitoredEndpoint) {
		val future = scheduler.schedule(
			WatchTask(endpoint, this.endpointRepository, this.resultRepository),
			PeriodicTrigger(endpoint.monitoredInterval * 1000)
		)
		this.tasks.put(endpoint.id, future!!)
	}

	@EventListener
	/// Starts all tasks on context start (application start).
    fun handleContextStart(event: ContextStartedEvent) {
		this.endpointRepository.findAll().forEach({
			endpoint -> this.startTask(endpoint)
		})
    }

	@TransactionalEventListener
	/// Starts and stops tasks according to endpoint changes.
	fun handleEndpointTransaction(event: EndpointChangedEvent) {
		val endpoint = this.endpointRepository.findEndpointById(event.endpoint_id)

		// remove task from hashmap
		// if the task exists, we cancel it
		// if endpoint still exists, we start a new task with new params
		val task = this.tasks.remove(event.endpoint_id)
		if (task != null) {
			task.cancel(true)
		}
		if (endpoint != null) {
			this.startTask(endpoint)
		}
	}
}