package productguild.oparty.matsosik.restaurants.repository

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import productguild.oparty.matsosik.restaurants.domain.Restaurant

@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class RestaurantRepositoryTest(
	private val entityManager: EntityManager,
	private val restaurantRepository: RestaurantRepository,
) : DescribeSpec({
	isolationMode = IsolationMode.InstancePerLeaf
	val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

	beforeEach {
		val restaurant1 = Restaurant(
			displayName = "1번 식당 장소",
			name = "테스트 기준 장소 (거리 테스트용)",
			thumbnail = "https://image_url",
			address = "전라북도 전주시 완산구 동완산동 451-3",
			geometryFactory.createPoint(Coordinate(127.1425492, 35.80841378)),
		)
		val restaurant2 = Restaurant(
			displayName = "2번 식당 장소",
			name = "1번과 18.62km(구글맵 기준) 떨어짐",
			thumbnail = "https://image_url",
			address = "전라북도 완주군 봉동읍 장구리",
			geometryFactory.createPoint(Coordinate(127.1129, 35.9738)),
		)
		val restaurant3 = Restaurant(
			displayName = "3번 식당 장소",
			name = "3번 식당 이름",
			thumbnail = "https://image_url",
			address = "전라북도 전주시 덕진구 전진로 107-13",
			geometryFactory.createPoint(Coordinate(127.1793985, 35.8451205)),
		)
		val restaurant4 = Restaurant(
			displayName = "샤브샤브 (롯데마트)",
			name = "샤브보트 롯데마트 서초점",
			thumbnail = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzExMTdfMjUz%2FMDAxNzAwMTkyNTYxNjUy.-FS-UOMm61Q8veLrEMIvQHVrgQzd9IxvbEQQEbNh50Ig.h5S23QDcFtY8hQWGHG2Um4sAUTRDA-oiX0Sq0tJLxM8g.JPEG.a_zum_koya%2F20231117%25A3%25DF114135.jpg&type=sc960_832",
			address = "서울 서초구 서초대로38길 12 롯데마트 내",
			geometryFactory.createPoint(Coordinate(127.0061464, 37.4902536)),
		)
		val restaurant5 = Restaurant(
			displayName = "우야 (장어덮밥)",
			name = "우야 서초점",
			thumbnail = "https://d12zq4w4guyljn.cloudfront.net/750_750_20220929055414_photo2_OQsttOHxMH8D.jpg",
			address = "서울특별시 서초구 서초대로42길 41 2층",
			geometryFactory.createPoint(Coordinate(127.0093969, 37.4902836)),
		)
		val restaurant6 = Restaurant(
			displayName = "서초정육식당",
			name = "O파티 불고기 맛집",
			thumbnail = "https://search.pstatic.net/common/?src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20191001_113%2F1569859621119h9hzs_JPEG%2FlX9h7KXb2uoC2VSWwXUuYI_U.jpg",
			address = "서울 서초구 서초대로38길 51",
			geometryFactory.createPoint(Coordinate(127.0068856, 37.4889892)),
		)

		restaurantRepository.saveAll(listOf(restaurant1, restaurant2, restaurant3, restaurant4, restaurant5, restaurant6))
		entityManager.flush()
		entityManager.clear()
	}

	describe("findNearRestaurantsFromCurrentPoint") {
		context("현재 위치 기준 4.23km, 14.71km, 5.84km 떨어진 식당 3곳이 존재하고, 반경 5.8km 안에 있는 식당 정보를 조회할 때") {
			it("5.8km 안의 식당 정보 1곳을 리스트로 반환한다.") {
				val result = restaurantRepository.findNearRestaurantsFromCurrentPoint(
					// 1번과는 4.23km, 2번과는 14.71km, 3번과는 5.84km 떨어진 장소
					currentPoint = geometryFactory.createPoint(Coordinate(127.1154, 35.83841)),
					rangeMeter = 5800 // 반경 5.8km
				)

				// 5.8km 반경에 있는 식당은 1번 식당 1곳이 유일.
				result.size shouldBe 1
				result[0].geoLocation.x shouldBeEqual 127.1425492
				result[0].geoLocation.y shouldBeEqual 35.80841378
			}
		}

		context("현재 위치 기준 70m, 340m, 201m 떨어진 식당 3곳이 존재하고, 반경 300m 안에 있는 식당 정보를 조회할 때") {
			it("300m 내에 있는 식당 정보 2곳을 반환한다.") {
				val result = restaurantRepository.findNearRestaurantsFromCurrentPoint(
					// 샤브샤브 (롯데마트) 70m, 우야 (장어덮밥) 340m, 서초정육식당과 201m 떨어진 장소 (마제스타시티 위치)
					currentPoint = geometryFactory.createPoint(Coordinate(127.0056469, 37.4904845)),
					rangeMeter = 300 // 반경 0.3km
				)

				result.size shouldBe 2
			}
		}
	}
})
