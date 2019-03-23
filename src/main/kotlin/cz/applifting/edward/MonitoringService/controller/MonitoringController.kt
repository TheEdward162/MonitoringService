package cz.applifting.edward.MonitoringService

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.ExceptionHandler

import java.time.LocalDateTime

@RestController
@RequestMapping("/monitoring")
class MonitoringController {
	@RequestMapping("", method=[RequestMethod.GET])
	fun listMonitorings(): List<MonitoringResult> {
		var list = mutableListOf<MonitoringResult>()
		return list
	}

	@RequestMapping("/{id}", method=[RequestMethod.GET])
	fun getMonitoring(): MonitoringResult {
		return MonitoringResult(
			0,
			LocalDateTime.now(),
			200,
			"load of pay",
			0
		)
	}
}