package cz.applifting.edward.MonitoringService

import java.net.URI
import java.lang.reflect.Type

import org.assertj.core.api.Assertions.*

import com.fasterxml.jackson.databind.JsonNode

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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class IntegrationTests(
	@Autowired private val restTemplate: TestRestTemplate,
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository
) {
	private val user = User("test", "test@test.com", "123")
	private val endpoint = MonitoredEndpoint("test", "test.url.com", 30, this.user)
	
	@BeforeAll
	fun setup() {
		this.userRepository.save(this.user)
		this.endpointRepository.save(this.endpoint)
	}

	@Test
	fun `Context loads successfully`() {
	}

	@Test
	fun `GET endpoint returns Error(401, "Unauthorized")`() {
		val entity = restTemplate.getForEntity<Status>("/endpoint")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
		assertThat(entity.body).isEqualTo(Status(401, "Unauthorized"))
	}

	@Test
	fun `Authorized GET endpoint returns list of endpoints`() {
		val request = RequestEntity.get(URI("/endpoint")).header("Authorization", "Bearer " + this.user.accessToken).build()
		val response = this.restTemplate.exchange(request, JsonNode::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		
		val body = response.body!!
		assertThat(body.isArray()).isTrue()
		
		val node = body[0]
		assertThat(node.get("name").asText()).isEqualTo(this.endpoint.name)
		assertThat(node.get("url").asText()).isEqualTo(this.endpoint.url)
		assertThat(node.get("monitoredInterval").asInt()).isEqualTo(this.endpoint.monitoredInterval)
	}

	@Test
	fun `Authorized GET enpoint test returns one endpoint`() {
		val request = RequestEntity.get(URI("/endpoint/test")).header("Authorization", "Bearer " + this.user.accessToken).build()
		val response = this.restTemplate.exchange(request, JsonNode::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

		val node: JsonNode = response.body!!
		assertThat(node.get("name").asText()).isEqualTo(this.endpoint.name)
		assertThat(node.get("url").asText()).isEqualTo(this.endpoint.url)
		assertThat(node.get("monitoredInterval").asInt()).isEqualTo(this.endpoint.monitoredInterval)
	}

	// @Test
	// fun `Authorized POST enpoint test adds endpoint`() {
	// 	val request = RequestEntity.get(URI("/endpoint")).header("Authorization", "Bearer " + this.user.accessToken).build()
	// 	val response = this.restTemplate.exchange(request, MonitoredEndpoint::class.java)

	// 	assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
	// 	assertThat(response.body).isEqualTo(this.endpoint)
	// }

	// @Test
	// fun `Authorized POST enpoint test updates endpoint`() {
	// 	val request = RequestEntity.get(URI("/endpoint")).header("Authorization", "Bearer " + this.user.accessToken).build()
	// 	val response = this.restTemplate.exchange(request, MonitoredEndpoint::class.java)

	// 	assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
	// 	assertThat(response.body).isEqualTo(this.endpoint)
	// }

	@Test
	fun `Authorized DELETE endpoint test deletes endpoint`() {
		val request = RequestEntity.delete(URI("/endpoint/test")).header("Authorization", "Bearer " + this.user.accessToken).build()
		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).isEqualTo(Status(200, "OK"))
		// assertThat(this.endpointRepository.findEndpointByNameAndUser("test", this.user)).isNull()

		// @Order annotation from JUnit5.4 doesn't work
		this.endpointRepository.save(this.endpoint)
	}

	@AfterAll
	fun teardown() {
		val user = this.userRepository.findByAccessToken(this.user.accessToken)
		if (user != null)
			this.userRepository.delete(user)
	}
}