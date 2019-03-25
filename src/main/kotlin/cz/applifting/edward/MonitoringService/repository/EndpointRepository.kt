package cz.applifting.edward.MonitoringService;

import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

public interface EndpointRepository : CrudRepository<MonitoredEndpoint, Int> {
	fun findEndpointById(id: Int): MonitoredEndpoint?
	fun findEndpointByNameAndUser(name: String, user: User): MonitoredEndpoint?
	fun findByUser(user: User): Iterable<MonitoredEndpoint>

	@Transactional
	@Modifying
	@Query("UPDATE MonitoredEndpoint e SET e.lastCheck = ?1 WHERE e.id = ?2")
	fun saveLastCheckById(lastCheck: LocalDateTime?, id: Int)
}