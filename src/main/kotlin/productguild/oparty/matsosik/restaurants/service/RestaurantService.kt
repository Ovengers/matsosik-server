package productguild.oparty.matsosik.restaurants.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import productguild.oparty.matsosik.global.util.GeometryUtil
import productguild.oparty.matsosik.restaurants.domain.Restaurant
import productguild.oparty.matsosik.restaurants.dto.request.list.RestaurantSearchDto
import productguild.oparty.matsosik.restaurants.dto.response.list.RestaurantResponseDto
import productguild.oparty.matsosik.restaurants.dto.response.list.RestaurantsResponseDto
import productguild.oparty.matsosik.restaurants.repository.RestaurantRepository

@Transactional(readOnly = true)
@Service
class RestaurantService(
	private val restaurantRepository: RestaurantRepository,
	private val geometryUtil: GeometryUtil,
) {
	val log = KotlinLogging.logger { }

	fun searchRestaurants(searchRequest: RestaurantSearchDto): RestaurantsResponseDto {
		log.info { "request : $searchRequest" }
		val currentPoint = geometryUtil.convertLocationToPoint(searchRequest.latitude!!, searchRequest.longitude!!)
		var result = restaurantRepository.findNearRestaurantsFromCurrentPoint(
			currentPoint = currentPoint, rangeMeter = searchRequest.rangeMeter!!
		)
		result = filterByDisplayName(searchRequest, result)

		return RestaurantsResponseDto(restaurants = result.map { RestaurantResponseDto(it) })
	}

	private fun filterByDisplayName(
		searchRequest: RestaurantSearchDto,
		result: List<Restaurant>
	): List<Restaurant> {
		searchRequest.displayName?.let { searchDisplayName ->
			return result.filter { restaurant ->
				restaurant.displayName?.contains(searchDisplayName) ?: false
			}
		} ?: return result
	}
}
