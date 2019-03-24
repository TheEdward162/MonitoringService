package cz.applifting.edward.MonitoringService;

import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

import org.springframework.transaction.annotation.Transactional

public interface ResultRepository : CrudRepository<MonitoringResult, Int> {
	fun findAllByEndpoint(endpoint: MonitoredEndpoint): Iterable<MonitoringResult>
	fun findTop10ByEndpointOrderByCheckTimeDesc(endpoint: MonitoredEndpoint): Iterable<MonitoringResult>

	@Transactional
	@Modifying
	@Query("DELETE FROM MonitoringResult r WHERE r.endpoint = ?1 AND r NOT IN ?2")
	fun deleteByEndpointNotIn(endpoint: MonitoredEndpoint, list: Iterable<MonitoringResult>)
}