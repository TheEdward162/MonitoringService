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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResultTests(
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository,
	@Autowired private val resultRepository: ResultRepository
) {
	@Transactional
	@Test
	fun `Result repository top 10 and delete queries work`() {
		val user = User("test_result_1", "test_result_1", "test_result_1")
		val endpoint = MonitoredEndpoint("test_result_1", "http://invalid.test.url.com", 30, user)
		this.userRepository.save(user)
		this.endpointRepository.save(endpoint)
		
		for (i in 0 .. 12) {
			val result = MonitoringResult(200, i.toString(), endpoint)
			this.resultRepository.save(result)
		}

		val topTen = this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(endpoint)
		assertThat(topTen).hasSize(10)

		this.resultRepository.deleteByEndpointNotIn(endpoint, topTen)
		assertThat(this.resultRepository.findAllByEndpoint(endpoint)).hasSize(10)

		this.userRepository.delete(user)
	}

	@Transactional
	@Test
	fun `(Internet connection required) Watch task opens connection, saves to database, limits results`() {
		val user = User("test_result_2", "test_result_2", "test_result_2")
		val endpoint = MonitoredEndpoint("test_result_2", "http://example.com", 30, user)
		this.userRepository.save(user)
		this.endpointRepository.save(endpoint)
		
		val task = WatchTask(endpoint, this.endpointRepository, this.resultRepository)
		for (i in 0 .. 10) {
			task.run()
		}

		val results = this.resultRepository.findAllByEndpoint(endpoint)
		assertThat(results).hasSize(10)
		assertThat(results).allMatch({
			r -> r.statusCode != -1
		})

		this.userRepository.delete(user)
	}

	@Transactional
	@Test
	fun `Watch task opens connection, handles failure, saves to database, limits results`() {
		val user = User("test_result_3", "test_result_3", "test_result_3")
		val endpoint = MonitoredEndpoint("test_result_3", "http://should-not.exist.yo.yo.yo", 30, user)
		this.userRepository.save(user)
		this.endpointRepository.save(endpoint)
		
		val task = WatchTask(endpoint, this.endpointRepository, this.resultRepository)
		for (i in 0 .. 10) {
			task.run()
		}

		val results = this.resultRepository.findAllByEndpoint(endpoint)
		assertThat(results).hasSize(10)
		assertThat(results).allMatch({
			res -> res.statusCode == -1 && res.payload == null
		})

		this.userRepository.delete(user)
	}
}