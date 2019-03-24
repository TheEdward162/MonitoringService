package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.JoinColumn
import javax.persistence.FetchType

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDateTime

@Entity
@Table(
	uniqueConstraints = [
    	UniqueConstraint(columnNames = ["name", "user_id"])
	]
)
data class MonitoredEndpoint(
	@Column(columnDefinition = "varchar(64) NOT NULL")
	val name: String,

	@Column(columnDefinition = "text NOT NULL")
	val url: String,

	@Column(nullable=false)
	val monitoredInterval: Long,
	
	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	val user: User,


	@Column(columnDefinition = "timestamp NOT NULL")
	val creation: LocalDateTime = LocalDateTime.now(),

	val lastCheck: LocalDateTime? = null,

	@JsonIgnore
	@OneToMany(mappedBy = "endpoint", fetch = FetchType.LAZY,  cascade = [CascadeType.ALL])
	@OnDelete(action = OnDeleteAction.CASCADE)
	val results: List<MonitoringResult> = listOf(),

	@JsonIgnore
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int = 0
)