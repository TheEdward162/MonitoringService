package cz.applifting.edward.MonitoringService

enum class Status(val code: Int, val message: String) {
	OK(200, "Ok"),

	BadRequest(400, "Bad Request"),
	Unauthorized(401, "Unauthorized"),
	NotFound(404, "Not Found"),

	ServerError(500, "Server Error")
}