package cz.applifting.edward.MonitoringService

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.OneToMany

import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
data class User(
	@JsonIgnore
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	val id: Long = 0,
	
	@Column(columnDefinition = "varchar(64) NOT NULL UNIQUE")
	val userName: String,
	
	@Column(columnDefinition = "varchar(64) NOT NULL UNIQUE")
	val email: String,
	
	@Column(columnDefinition = "char(36) NOT NULL UNIQUE")
	val accessToken: String
)