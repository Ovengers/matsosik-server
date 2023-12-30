package productguild.oparty.matsosik.restaurants.repository

import org.locationtech.jts.geom.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import productguild.oparty.matsosik.restaurants.domain.Restaurant

interface RestaurantRepository : JpaRepository<Restaurant, Long> {
	@Query(
		value = "SELECT * " +
			"FROM restaurants " +
			"WHERE ST_DistanceSpheroid(restaurants.geo_location, :currentPoint) < :rangeMeter ",
		nativeQuery = true
	)
	fun findNearRestaurantsFromCurrentPoint(
		@Param("currentPoint") currentPoint: Point,
		@Param("rangeMeter") rangeMeter: Int,
	): List<Restaurant>

	fun findByDisplayName(displayName: String): Restaurant?

	fun findByName(name: String): Restaurant?
}
