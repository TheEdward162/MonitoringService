package cz.applifting.edward.MonitoringService;

import org.springframework.data.repository.CrudRepository

public interface EndpointRepository : CrudRepository<MonitoredEndpoint, Long> {
	fun findEndpointByNameAndUser(name: String, user: User): MonitoredEndpoint?
	fun findByUser(user: User): Iterable<MonitoredEndpoint>

	fun deleteEndpointByNameAndUser(name: String, user: User)
}