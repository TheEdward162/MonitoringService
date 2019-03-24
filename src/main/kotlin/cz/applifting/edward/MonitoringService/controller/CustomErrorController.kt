package cz.applifting.edward.MonitoringService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
public class CustomErrorController: ErrorController {
    @RequestMapping("/error")
    fun error(_request: HttpServletRequest, response: HttpServletResponse): Status {
		val status = response.getStatus()

		val message: String
		when (status) {
			// "/error" mapping exists but we don't want to return status code 200 for it
			200 -> {
				response.setStatus(404)
				return Status(404, "Not Found")
			}
			400 -> message = "Bad Request"
			401 -> {
				response.setHeader("WWW-Authenticate", "Bearer")
				message = "Unauthorized"
			}
			404 -> message = "Not Found"
			else -> message = "Server Error"
		}
		
		return Status(status, message)
    }

    override fun getErrorPath(): String {
        return "/error";
    }
}