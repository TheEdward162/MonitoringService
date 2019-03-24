package cz.applifting.edward.MonitoringService;

import org.springframework.data.repository.CrudRepository

import org.springframework.transaction.annotation.Transactional

public interface EndpointRepository : CrudRepository<MonitoredEndpoint, Long> {
	fun findEndpointByNameAndUser(name: String, user: User): MonitoredEndpoint?
	fun findByUser(user: User): Iterable<MonitoredEndpoint>

	@Transactional
	fun deleteEndpointByNameAndUser(name: String, user: User)
}