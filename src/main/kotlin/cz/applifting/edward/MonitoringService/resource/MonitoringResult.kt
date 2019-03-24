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

	@Column(columnDefinition = "text NOT NULL")
	val payload: String,

	@Column(columnDefinition = "timestamp NOT NULL")
	val check_time: LocalDateTime = LocalDateTime.now(),

	@JsonIgnore
	@ManyToOne(optional = false, cascade = [CascadeType.ALL])
	@JoinColumn(name = "endpoint_id")
	val endpoint: MonitoredEndpoint,

	@JsonIgnore
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int = 0
)