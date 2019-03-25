package cz.applifting.edward.MonitoringService

import java.net.URL
import java.net.MalformedURLException

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDateTime

@RestController
@RequestMapping("/endpoint")
class RestController(
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository,
	@Autowired private val resultRepository: ResultRepository,

	@Autowired private val eventPublisher: ApplicationEventPublisher
) {
	/// Extracts token from HTTP header value in format `Authorization: Bearer TOKEN`.
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

	/// Extracts token from header, attempts to find the owner of the token.
	///
	/// Throws `NoSuchUserException` if no user is found.
	private fun getUserByToken(authHeaderValue: String?): User {
		val userToken = this.extractToken(authHeaderValue);
		val user = this.userRepository.findByAccessToken(userToken);
		if (user == null)
			throw NoSuchUserException()
		
		return user
	}

	@RequestMapping("", method = [ RequestMethod.GET ])
	fun listEndpoints(
		@RequestHeader("Authorization") authHeaderValue: String?
	): List<MonitoredEndpoint> {
		val user = this.getUserByToken(authHeaderValue)
		return this.endpointRepository.findByUser(user).toList()
	}

	@RequestMapping("/{name}", method = [ RequestMethod.GET ])
	fun getEndpoint(
		@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String
	): MonitoredEndpoint {
		val user = this.getUserByToken(authHeaderValue)
		val endpoint = this.endpointRepository.findEndpointByNameAndUser(name, user)
		if (endpoint == null)
			throw EndpointNotFoundException()
		
		return endpoint
	}

	@Transactional
	@RequestMapping("/{name}", method = [ RequestMethod.POST ])
	fun updateEndpoint(
		@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String,
		@RequestParam("url") url: String?, @RequestParam("interval") interval: Long? 
	): Status {
		val user = this.getUserByToken(authHeaderValue)
		val endpoint = this.endpointRepository.findEndpointByNameAndUser(name, user)

		if (endpoint == null && (url == null || interval == null || interval < 1))
			throw InvalidParamsException()
		if (endpoint != null && (url == null && (interval == null || interval < 1)))
			throw InvalidParamsException()
		// validate url format
		if (url != null) {
			try {
				URL(url)
			} catch (e: MalformedURLException) {
				throw InvalidParamsException()
			}
		}

		if (endpoint == null) {
			val newEndpoint = this.endpointRepository.save(MonitoredEndpoint(
				name,
				url!!, // already checked above
				interval!!, // already checked above
				user
			))
			this.eventPublisher.publishEvent(EndpointChangedEvent(newEndpoint.id))
		} else {
			this.endpointRepository.save(endpoint.copy(
				url = url ?: endpoint.url,
				monitoredInterval = interval ?: endpoint.monitoredInterval
			))
			this.eventPublisher.publishEvent(EndpointChangedEvent(endpoint.id))
		}

		return Status.OK
	}

	@Transactional
	@RequestMapping("/{name}", method = [ RequestMethod.DELETE ])
	fun deleteEndpoint(
		@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String
	): Status {
		val user = this.getUserByToken(authHeaderValue)
		val endpoint = this.endpointRepository.findEndpointByNameAndUser(name, user)
		if (endpoint == null)
			throw EndpointNotFoundException()

		this.endpointRepository.delete(endpoint)
		this.eventPublisher.publishEvent(EndpointChangedEvent(endpoint.id))

		return Status.OK
	}

	@RequestMapping("/{name}/results", method = [ RequestMethod.GET ])
	fun listResults(
		@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String
	): List<MonitoringResult> {
		val endpoint = this.getEndpoint(authHeaderValue, name)
		return this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(endpoint).toList()
	}

	@RequestMapping("/{name}/results/{offset}", method = [ RequestMethod.GET ])
	fun getResult(
		@RequestHeader("Authorization") authHeaderValue: String?, @PathVariable name: String,
		@PathVariable offset: Int
	): MonitoringResult {
		val endpoint = this.getEndpoint(authHeaderValue, name)
		val result = this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(endpoint).elementAtOrNull(offset)
		if (result == null)
			throw ResultNotFoundException()
		
		return result
	}
}