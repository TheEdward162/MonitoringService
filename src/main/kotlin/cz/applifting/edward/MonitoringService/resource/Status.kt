package cz.applifting.edward.MonitoringService

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class Status(val code: Int, val message: String) {
	OK(200, "Ok"),

	BadRequest(400, "Bad Request"),
	Unauthorized(401, "Unauthorized"),
	NotFound(404, "Not Found"),

	ServerError(500, "Server Error");

	companion object {
        @JvmStatic @JsonCreator
        fun fromCode(@JsonProperty("code") code: Int): Status {
			when (code) {
				200 -> return Status.OK

				400 -> return Status.BadRequest
				401 -> return Status.Unauthorized
				404 -> return Status.NotFound

				else -> return Status.ServerError
			}
        }
    }
}