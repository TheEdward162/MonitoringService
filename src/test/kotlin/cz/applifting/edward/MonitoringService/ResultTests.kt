package cz.applifting.edward.MonitoringService

import java.net.URI
import java.lang.reflect.Type

import org.assertj.core.api.Assertions.*

import com.fasterxml.jackson.databind.JsonNode

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class ResultTests(
	@Autowired private val restTemplate: TestRestTemplate,
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository,
	@Autowired private val resultRepository: ResultRepository
) {
	private val user = User("test2", "test2@test.com", "456")
	private val endpoint = MonitoredEndpoint("test", "test.url.com", 30, this.user)
	
	@BeforeAll
	fun setup() {
		this.userRepository.save(this.user)
		this.endpointRepository.save(this.endpoint)
	}

	@Test
	fun `Result repository top 10 and delete queries work`() {
		for (i in 0 .. 12) {
			val result = MonitoringResult(200, i.toString(), this.endpoint)
			this.resultRepository.save(result)
		}

		val topTen = this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(this.endpoint)
		assertThat(topTen).hasSize(10)

		this.resultRepository.deleteByEndpointNotIn(this.endpoint, topTen)
		assertThat(this.resultRepository.findAll()).hasSize(10)
	}

	@AfterAll
	fun teardown() {
		val user = this.userRepository.findByAccessToken(this.user.accessToken)
		if (user != null)
			this.userRepository.delete(user)
	}
}