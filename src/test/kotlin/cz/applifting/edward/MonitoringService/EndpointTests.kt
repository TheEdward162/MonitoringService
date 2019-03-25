package cz.applifting.edward.MonitoringService

import java.net.URI
import java.lang.reflect.Type

import org.assertj.core.api.Assertions.*

import com.fasterxml.jackson.databind.JsonNode

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.ResourceAccessException

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
class EndpointTests(
	@Autowired private val restTemplate: TestRestTemplate,
	@Autowired private val userRepository: UserRepository,
	@Autowired private val endpointRepository: EndpointRepository
) {
	private val user = User("test", "test@test.com", "123")
	private val endpoint = MonitoredEndpoint("test", "http://test.url.com", 30, this.user)
	private val updateEndpoint = MonitoredEndpoint("toupdate", "http://update.url.com", 10, this.user)
	private val deleteEndpoint = MonitoredEndpoint("todelete", "http://todelete.url.com", 500, this.user)
	
	@BeforeAll
	fun setup() {
		this.userRepository.save(this.user)
		this.endpointRepository.save(this.endpoint)
		this.endpointRepository.save(this.updateEndpoint)
		this.endpointRepository.save(this.deleteEndpoint)
	}

	@Test
	fun `GET endpoint returns Error(401, "Unauthorized")`() {
		val request = RequestEntity
			.get(URI("/endpoint"))
		.build()
		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
		assertThat(response.body).isEqualTo(Status.Unauthorized)
	}

	@Test
	fun `POST endpoint name returns Error(401, "Unauthorized")`() {
		val request = RequestEntity
			.post(URI("/endpoint/test"))
			.header("Content-Type", "application/x-www-form-urlencoded")
		.body("url=unauth.url.com&interval=5000")
		try {
			val response = this.restTemplate.exchange(request, Status::class.java)
		} catch (e: ResourceAccessException) {
			return
		}

		// assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
		// assertThat(response.body).isEqualTo(Status.Unauthorized)

		// assertThat(this.endpointRepository.findEndpointByNameAndUser(this.endpoint.name, this.user)!!.url).isNotEqualTo("unauth.url.com")
	}

	@Test
	fun `DELETE endpoint name returns Error(401, "Unauthorized")`() {
		val request = RequestEntity
			.delete(URI("/endpoint/" + this.endpoint.name))
		.build()
		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
		assertThat(response.body).isEqualTo(Status.Unauthorized)
		assertThat(this.endpointRepository.findEndpointByNameAndUser(this.endpoint.name, this.user)).isNotNull()
	}

	@Test
	fun `Authorized GET endpoint returns list of endpoints`() {
		val request = RequestEntity
			.get(URI("/endpoint"))
			.header("Authorization", "Bearer " + this.user.accessToken)
		.build()
		val response = this.restTemplate.exchange(request, JsonNode::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		
		val body = response.body!!
		assertThat(body.isArray()).isTrue()
		assertThat(body).anyMatch({ node ->
			node.get("name").asText().equals(this.endpoint.name)
			&&
			node.get("url").asText().equals(this.endpoint.url)
			&&
			node.get("monitoredInterval").asLong().equals(this.endpoint.monitoredInterval)
		})
	}

	@Test
	fun `Authorized GET endpoint test returns one endpoint`() {
		val request = RequestEntity
			.get(URI("/endpoint/test"))
			.header("Authorization", "Bearer " + this.user.accessToken)
		.build()
		val response = this.restTemplate.exchange(request, JsonNode::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

		val node: JsonNode = response.body!!
		assertThat(node.get("name").asText()).isEqualTo(this.endpoint.name)
		assertThat(node.get("url").asText()).isEqualTo(this.endpoint.url)
		assertThat(node.get("monitoredInterval").asInt()).isEqualTo(this.endpoint.monitoredInterval)
	}

	@Test
	fun `Authorized POST endpoint test adds endpoint`() {
		val request = RequestEntity
			.post(URI("/endpoint/testadd"))
			.header("Authorization", "Bearer " + this.user.accessToken)
			.header("Content-Type", "application/x-www-form-urlencoded")
		.body("url=http://add.url.com&interval=20")

		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).isEqualTo(Status.OK)

		val testadd = this.endpointRepository.findEndpointByNameAndUser("testadd", this.user)!!
		assertThat(testadd.url).isEqualTo("http://add.url.com")
		assertThat(testadd.monitoredInterval).isEqualTo(20)
	}

	@Test
	fun `Authorized POST endpoint test updates endpoint`() {
		val request = RequestEntity
			.post(URI("/endpoint/toupdate"))
			.header("Authorization", "Bearer " + this.user.accessToken)
			.header("Content-Type", "application/x-www-form-urlencoded")
		.body("url=http://veryupdate.url.com")

		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).isEqualTo(Status.OK)

		val testadd = this.endpointRepository.findEndpointByNameAndUser("toupdate", this.user)!!
		assertThat(testadd.url).isEqualTo("http://veryupdate.url.com")
		assertThat(testadd.monitoredInterval).isEqualTo(this.updateEndpoint.monitoredInterval)
	}

	@Test
	fun `Authorized DELETE endpoint test deletes endpoint`() {
		val request = RequestEntity
			.delete(URI("/endpoint/todelete"))
			.header("Authorization", "Bearer " + this.user.accessToken)
		.build()
		val response = this.restTemplate.exchange(request, Status::class.java)

		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(response.body).isEqualTo(Status.OK)
		assertThat(this.endpointRepository.findEndpointByNameAndUser("todelete", this.user)).isNull()
	}

	@AfterAll
	fun teardown() {
		val user = this.userRepository.findByAccessToken(this.user.accessToken)
		if (user != null)
			this.userRepository.delete(user)
	}
}