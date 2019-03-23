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
    fun error(request: HttpServletRequest, response: HttpServletResponse): Error {
		val status = response.getStatus()
		
		if (status == 200) return Error(404, "Not Found")

		val message: String
		when (status) {
			401 -> message = "Unauthorized"
			404 -> message = "Not Found"
			else -> message = "Server Error"
		}
		
		return Error(status, message)
    }

    override fun getErrorPath(): String {
        return "/error";
    }
}