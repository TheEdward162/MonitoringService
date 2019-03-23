package cz.applifting.edward.MonitoringService

import java.time.LocalDateTime

data class MonitoringResult(
	val id: Int,
	val check: 	LocalDateTime,
	val statusCode: Int,
	val payload: String,
	val monitoredEndpointId: Int
)