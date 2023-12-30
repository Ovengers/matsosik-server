package productguild.oparty.matsosik.restaurants.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import productguild.oparty.matsosik.restaurants.dto.request.RestaurantCreateDto
import java.time.LocalDateTime

@Table(
	name = "restaurants",
	indexes = [
		Index(name = "idx__display_name__created_at", columnList = "display_name, created_at"),
		Index(name = "idx__geo_location", columnList = "geo_location"),
	]
)
@Entity
class Restaurant(
	var displayName: String? = null,
	var name: String,
	@Column(columnDefinition = "TEXT")
	var thumbnail: String? = null,
	var address: String,
	@Column(columnDefinition = "geometry(Point, 4326)")
	var geoLocation: Point,
	@Column(columnDefinition = "TEXT")
	var extraInfo: String? = null,
	val createdAt: LocalDateTime = LocalDateTime.now(),
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0

	var updatedAt: LocalDateTime? = null

	companion object {
		fun create(
			request: RestaurantCreateDto,
			geometryFactory: GeometryFactory,
		): Restaurant {
			val coordinate = Coordinate(request.longitude, request.latitude)

			return Restaurant(
				displayName = request.displayName,
				name = request.name,
				address = request.address,
				geoLocation = geometryFactory.createPoint(coordinate),
				extraInfo = request.extraInfo,
			)
		}
	}
}