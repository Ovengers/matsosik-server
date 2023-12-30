package productguild.oparty.matsosik.restaurants.dto.request.list

data class RestaurantSearchDto(
	var displayName: String? = null,
	val latitude: Double? = 37.4904845,
	val longitude: Double? = 127.0056469,
	val rangeMeter: Int? = 1000,
)
