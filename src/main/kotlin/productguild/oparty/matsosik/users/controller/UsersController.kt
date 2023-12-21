package productguild.oparty.matsosik.users.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/users")
@RestController
class UsersController {
	@GetMapping("/my-info")
	fun myInfoDetail(): ResponseEntity<Any> {
		// TODO : 구현해야함
		return ResponseEntity(mapOf("result" to "hello"), HttpStatus.OK)
	}
}