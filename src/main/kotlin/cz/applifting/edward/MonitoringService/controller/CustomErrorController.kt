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
    fun error(response: HttpServletResponse): Status {
		val status = response.getStatus()
		when (status) {
			// "/error" mapping exists but we don't want to return status code 200 for it
			200 -> {
				response.setStatus(404)
				return Status.NotFound
			}
			400 -> return Status.BadRequest
			401 -> {
				response.setHeader("WWW-Authenticate", "Bearer")
				return Status.Unauthorized
			}
			404 -> return Status.NotFound
			else -> return Status.ServerError
		}
    }

    override fun getErrorPath(): String {
        return "/error";
    }
}