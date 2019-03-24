package cz.applifting.edward.MonitoringService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableScheduling
class MonitoringServiceApplication

fun main(args: Array<String>) {
	runApplication<MonitoringServiceApplication>(*args).start()
}
