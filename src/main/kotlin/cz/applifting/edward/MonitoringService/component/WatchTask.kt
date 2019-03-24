package cz.applifting.edward.MonitoringService

import java.net.URL
import java.net.HttpURLConnection
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException

import java.time.LocalDateTime

class WatchTask(
	private val endpoint: MonitoredEndpoint,
	private val endpointRepository: EndpointRepository,
	private val resultRepository: ResultRepository
) : Runnable {
	override fun run() {
		val rightNow = LocalDateTime.now()

		var statusCode: Int
		var payload: StringBuffer?
		try {
			val url = URL(endpoint.url)
			var connection = url.openConnection() as HttpURLConnection
			connection.setRequestMethod("GET")
			connection.setUseCaches(false)

			statusCode = connection.getResponseCode()
			
			var reader = BufferedReader(InputStreamReader(connection.getInputStream()))
			payload = StringBuffer()
			var inputLine: String? = reader.readLine()
			while (inputLine != null) {
				payload.append(inputLine)
				inputLine = reader.readLine()
			}
			reader.close()
		} catch (e: IOException) {
			statusCode = -1
			payload = null
		}

		println("Pinged " + endpoint.url + " with response status code " + statusCode)

		this.endpointRepository.saveLastCheckById(rightNow, endpoint.id)

		val result = MonitoringResult(statusCode, payload?.toString(), endpoint, rightNow)
		this.resultRepository.save(result)

		val lastTen = this.resultRepository.findTop10ByEndpointOrderByCheckTimeDesc(endpoint)
		this.resultRepository.deleteByEndpointNotIn(endpoint, lastTen)
	}
}