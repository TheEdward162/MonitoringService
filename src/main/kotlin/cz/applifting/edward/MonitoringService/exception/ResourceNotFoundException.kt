package cz.applifting.edward.MonitoringService

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException : RuntimeException() {
	
}