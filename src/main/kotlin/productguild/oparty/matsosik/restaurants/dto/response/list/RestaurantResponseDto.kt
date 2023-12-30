package productguild.oparty.matsosik.restaurants.dto.response.list

import productguild.oparty.matsosik.restaurants.domain.Restaurant

data class RestaurantResponseDto(
	val id: Long,
	val displayName: String?,
	val address: String,
	val thumbnail: String?,
	val latitude: Double,
	val longitude: Double,
) {
	constructor(
		restaurant: Restaurant,
	) : this(
		id = restaurant.id,
		displayName = restaurant.displayName,
		address = restaurant.address,
		thumbnail = restaurant.thumbnail,
		latitude = restaurant.geoLocation.y,
		longitude = restaurant.geoLocation.x,
	)
}
