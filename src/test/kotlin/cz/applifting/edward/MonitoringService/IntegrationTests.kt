package cz.applifting.edward.MonitoringService

import java.net.URI

import org.assertj.core.api.Assertions.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity

import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.AfterAll

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class IntegrationTests(
	@Autowired private val restTemplate: TestRestTemplate,
	@Autowired private val userRepository: UserRepository
) {
	@Test
	fun `Context loads successfully`() {
	}

	@Test
	fun `GET endpoint returns Error(401, "Unauthorized")`() {
		val entity = restTemplate.getForEntity<Status>("/endpoint")
		assertThat(entity.body).isEqualTo(Status(401, "Unauthorized"))
		assertThat(entity.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
	}

	@Test
	fun `Authorized GET endpoint returns list of endpoints`() {
		val user = User(0, "test", "test@test.com", "123")
		this.userRepository.save(user)
		
		val request = RequestEntity.get(URI("/endpoint")).header("Authorization", "Bearer 123").build()
		val response = this.restTemplate.exchange(request, List::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).hasSize(0)
	}

	@AfterAll
	fun teardown() {
		val user = this.userRepository.findByAccessToken("123")
		if (user != null)
			this.userRepository.delete(user)
	}
}