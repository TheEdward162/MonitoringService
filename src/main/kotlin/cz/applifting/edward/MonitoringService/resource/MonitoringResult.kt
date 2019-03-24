package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Column

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDateTime

@Entity
data class MonitoringResult(
	@JsonIgnore
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	val id: Int,

	@Column(columnDefinition = "timestamp NOT NULL")
	val check_time: LocalDateTime,

	@Column(columnDefinition = "int NOT NULL")
	val statusCode: Int,

	@Column(columnDefinition = "text NOT NULL")
	val payload: String,

	@ManyToOne(optional=false)
	@JsonIgnore
	val endpoint: MonitoredEndpoint
)