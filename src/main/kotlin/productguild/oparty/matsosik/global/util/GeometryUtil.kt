package productguild.oparty.matsosik.global.util

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Component

@Component
class GeometryUtil {
	companion object {
		const val DEFAULT_SRID = 4326
		private val geometryFactory: GeometryFactory = GeometryFactory(PrecisionModel(), DEFAULT_SRID)
	}

	fun convertLocationToPoint(latitude: Double, longitude: Double): Point = geometryFactory.createPoint(
		Coordinate(longitude, latitude)
	)
}