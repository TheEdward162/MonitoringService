package cz.applifting.edward.MonitoringService

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.util.concurrent.atomic.AtomicLong
import java.time.LocalDateTime

@RestController
@RequestMapping("/endpoint")
class EndpointController {
    val counter = AtomicLong()

	@RequestMapping("", method=[RequestMethod.GET])
	fun listEndpoints(): List<MonitoredEndpoint> {
		var list = mutableListOf<MonitoredEndpoint>()
		list.add(MonitoredEndpoint(
			0,
			"point",
			"url.com",
			LocalDateTime.now(),
			LocalDateTime.now(),
			10,
			0
		))
		list.add(MonitoredEndpoint(
			1,
			"point2",
			"url2.com",
			LocalDateTime.now(),
			LocalDateTime.now(),
			51,
			2
		))

		return list
	}

	@RequestMapping("/{id}", method=[RequestMethod.GET])
	fun getEndpoint(@PathVariable id: Int): MonitoredEndpoint {
		if (id == 0) throw UnauthorizedException()
		
		return MonitoredEndpoint(
			id,
			"point",
			"url.com",
			LocalDateTime.now(),
			LocalDateTime.now(),
			10,
			0
		)
	}
}