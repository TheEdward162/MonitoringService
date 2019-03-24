package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Column
import javax.persistence.Table
import javax.persistence.UniqueConstraint

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDateTime

@Entity
@Table(
	uniqueConstraints = [
    	UniqueConstraint(columnNames = ["name", "user_id"])
	]
)
data class MonitoredEndpoint(
	@JsonIgnore
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	val id: Int,

	@Column(columnDefinition = "varchar(64) NOT NULL")
	val name: String,

	@Column(columnDefinition = "text NOT NULL")
	val url: String,

	@Column(columnDefinition = "timestamp NOT NULL")
	val creation: LocalDateTime,

	val lastCheck: LocalDateTime?,

	@Column(columnDefinition = "int NOT NULL")
	val monitoredInterval: Int,
	
	@JsonIgnore
	@ManyToOne(optional=false)
	val user: User
)