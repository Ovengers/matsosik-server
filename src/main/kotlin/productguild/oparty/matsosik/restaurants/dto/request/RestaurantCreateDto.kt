package productguild.oparty.matsosik.restaurants.dto.request

data class RestaurantCreateDto(
	val displayName: String? = null,
	val name: String,
	val address: String,
	val latitude: Double,
	val longitude: Double,
	val extraInfo: String? = null,
)
