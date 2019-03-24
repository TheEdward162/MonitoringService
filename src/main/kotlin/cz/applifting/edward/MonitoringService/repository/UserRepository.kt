package cz.applifting.edward.MonitoringService;

import org.springframework.data.repository.CrudRepository

public interface UserRepository : CrudRepository<User, Int> {
	fun findByAccessToken(accessToken: String): User?
}