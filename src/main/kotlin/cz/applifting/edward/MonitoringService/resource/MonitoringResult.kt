package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Column
import javax.persistence.CascadeType
import javax.persistence.JoinColumn

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDateTime

@Entity
data class MonitoringResult(
	@Column(columnDefinition = "int NOT NULL")
	val statusCode: Int,

	@Column(columnDefinition = "text")
	val payload: String?,

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "endpoint_id")
	val endpoint: MonitoredEndpoint,

	@Column(columnDefinition = "timestamp NOT NULL")
	val checkTime: LocalDateTime = LocalDateTime.now(),

	@JsonIgnore
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int = 0
)