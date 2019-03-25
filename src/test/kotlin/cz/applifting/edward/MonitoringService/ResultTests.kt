package cz.applifting.edward.MonitoringService

import java.net.URI
import java.lang.reflect.Type

import org.assertj.core.api.Assertions.*

import com.fasterxml.jackson.databind.JsonNode

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
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
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository,
	@Autowired private val resultRepository: ResultRepository
) {
	private val user1 = User("test_result_1", "test_result_1", "test_result_1")
	private val endpoint1 = MonitoredEndpoint("test_result_1", "http://invalid.test.url.com", 30, this.user1)

	private val user2 = User("test_result_2", "test_result_2", "test_result_2")
	private val endpoint2 = MonitoredEndpoint("test_result_2", "http://example.com", 30, this.user2)

	private val user3 = User("test_result_3", "test_result_3", "test_result_3")
	private val endpoint3 = MonitoredEndpoint("test_result_3", "http://should-not.exist.yo.yo.yo", 30, this.user3)
	
	@BeforeAll
	fun setup() {
		this.userRepository.save(this.user1)
		this.endpointRepository.save(this.endpoint1)
		
		this.userRepository.save(this.user2)
		this.endpointRepository.save(this.endpoint2)

		this.userRepository.save(this.user3)
		this.endpointRepository.save(this.endpoint3)
	}

	@Transactional
	@Test
	fun `Result repository top 10 and delete queries work`() {		
		for (i in 0 .. 12) {
			val result = MonitoringResult(200, i.toString(), this.endpoint1)
			this.resultRepository.save(result)
		}

		val topTen = this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(this.endpoint1)
		assertThat(topTen).hasSize(10)

		this.resultRepository.deleteByEndpointNotIn(this.endpoint1, topTen)
		assertThat(this.resultRepository.findAllByEndpoint(this.endpoint1)).hasSize(10)
	}

	@Transactional
	@Test
	fun `(Internet connection required) Watch task opens connection, saves to database, limits results`() {		
		val task = WatchTask(this.endpoint2, this.endpointRepository, this.resultRepository)
		for (i in 0 .. 10) {
			task.run()
		}

		val results = this.resultRepository.findAllByEndpoint(this.endpoint2)
		assertThat(results).hasSize(10)
		assertThat(results).allMatch({
			r -> r.statusCode != null
		})
	}

	@Transactional
	@Test
	fun `Watch task opens connection, handles failure, saves to database, limits results`() {		
		val task = WatchTask(this.endpoint3, this.endpointRepository, this.resultRepository)
		for (i in 0 .. 10) {
			task.run()
		}

		val results = this.resultRepository.findAllByEndpoint(this.endpoint3)
		assertThat(results).hasSize(10)
		assertThat(results).allMatch({
			res -> res.statusCode == null && res.payload == null
		})
	}

	@AfterAll
	fun teardown() {
		this.userRepository.delete(this.user1)
		this.userRepository.delete(this.user2)
		this.userRepository.delete(this.user3)
	}
}