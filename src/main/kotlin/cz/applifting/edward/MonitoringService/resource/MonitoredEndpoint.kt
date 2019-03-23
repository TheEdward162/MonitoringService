package cz.applifting.edward.MonitoringService

import java.time.LocalDateTime

data class MonitoredEndpoint(
	val id: Int,
	val name: String,
	val url: String,
	val creation: LocalDateTime,
	val lastCheck: LocalDateTime,
	val monitoredInterval: Int,
	val owner: Int
)