package cz.applifting.edward.MonitoringService

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

@RestController
@RequestMapping("/endpoint")
class EndpointController(
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository
) {
	/// Extracts token from HTTP header value in format `Authorization: Bearer token`.
	///
	/// Throws `UnauthorizedException` if `authHeaderValue` doesn't have the required format or is null.
	///
	/// While it would be also valid to return 400 Bad Request, 401 Unauthorized returns the `WWW-Authenticate` header
	/// which specifies what kind of authentification is required and therefore is preferable even for malformed header values.
	private fun extractToken(authHeaderValue: String?): String {
		if (authHeaderValue == null)
			throw UnauthorizedException()
		
		val splitValue = authHeaderValue.split(" ");
		if (splitValue.size != 2)
			throw UnauthorizedException()
		
		if (splitValue[0] != "Bearer")
			throw UnauthorizedException()
		
		return splitValue[1]
	}

	private fun getUserByToken(authHeaderValue: String?): User {
		val userToken = this.extractToken(authHeaderValue);
		val user = this.userRepository.findByAccessToken(userToken);
		if (user == null)
			throw NoSuchUserException()
		
		return user
	}

	@RequestMapping("", method=[RequestMethod.GET])
	fun listEndpoints(@RequestHeader("Authorization") authHeaderValue: String?): List<MonitoredEndpoint> {
		val user = this.getUserByToken(authHeaderValue)
		return this.endpointRepository.findByUser(user).toList()
	}

	@RequestMapping("/{name}", method=[RequestMethod.GET])
	fun getEndpoint(@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String): MonitoredEndpoint? {
		val user = this.getUserByToken(authHeaderValue)
		val endpoint = this.endpointRepository.findEndpointByNameAndUser(name, user)
		if (endpoint == null)
			throw EndpointNotFoundException()
		
		return endpoint
	}

	@RequestMapping("/{name}", method=[RequestMethod.DELETE])
	fun deleteEndpoint(@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String): Status {
		val user = this.getUserByToken(authHeaderValue)
		val was_deleted = this.endpointRepository.deleteEndpointByNameAndUser(name, user)
		if (!was_deleted)
			throw EndpointNotFoundException()
		
		return Status(200, "OK")
	}
}