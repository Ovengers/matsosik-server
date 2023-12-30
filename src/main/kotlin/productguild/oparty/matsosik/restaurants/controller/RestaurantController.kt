package productguild.oparty.matsosik.restaurants.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import productguild.oparty.matsosik.restaurants.dto.request.list.RestaurantSearchDto
import productguild.oparty.matsosik.restaurants.dto.response.list.RestaurantsResponseDto
import productguild.oparty.matsosik.restaurants.service.RestaurantService

@RequestMapping("/api/v1/restaurants")
@RestController
class RestaurantController(
	private val restaurantService: RestaurantService,
) {
	@GetMapping
	fun list(
		restaurantSearchDto: RestaurantSearchDto,
	): ResponseEntity<RestaurantsResponseDto> {
		val response = restaurantService.searchRestaurants(restaurantSearchDto)
		return ResponseEntity(response, HttpStatus.OK)
	}
}