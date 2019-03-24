package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.JoinColumn
import javax.persistence.CascadeType

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
data class User(	
	@Column(columnDefinition = "varchar(64) NOT NULL UNIQUE")
	val userName: String,
	
	@Column(columnDefinition = "varchar(64) NOT NULL UNIQUE")
	val email: String,
	
	@Column(columnDefinition = "char(36) NOT NULL UNIQUE")
	val accessToken: String,

	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@OnDelete(action = OnDeleteAction.CASCADE)
	val endpoints: List<MonitoredEndpoint> = listOf(),

	@JsonIgnore
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int = 0
)