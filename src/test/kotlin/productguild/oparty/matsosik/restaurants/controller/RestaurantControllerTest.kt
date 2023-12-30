package productguild.oparty.matsosik.restaurants.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import jakarta.persistence.EntityManager
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import productguild.oparty.matsosik.restaurants.domain.Restaurant
import productguild.oparty.matsosik.restaurants.repository.RestaurantRepository
import productguild.oparty.matsosik.testconfig.RestDocsConfiguration

@Transactional
@AutoConfigureRestDocs
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(RestDocsConfiguration::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RestaurantControllerTest(
	private val mockApi: MockMvc,
	private val entityManager: EntityManager,
	private val restaurantRepository: RestaurantRepository,
) : DescribeSpec({
	isolationMode = IsolationMode.InstancePerLeaf
	val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

	val mapper = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.withReflectionCacheSize(512)
			.configure(KotlinFeature.NullToEmptyCollection, false)
			.configure(KotlinFeature.NullToEmptyMap, false)
			.configure(KotlinFeature.NullIsSameAsDefault, false)
			.configure(KotlinFeature.StrictNullChecks, false)
			.build()
	)

	beforeEach {
		val restaurant1 = Restaurant(
			displayName = "샤브샤브 (롯데마트)",
			name = "샤브보트 롯데마트 서초점",
			thumbnail = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzExMTdfMjUz%2FMDAxNzAwMTkyNTYxNjUy.-FS-UOMm61Q8veLrEMIvQHVrgQzd9IxvbEQQEbNh50Ig.h5S23QDcFtY8hQWGHG2Um4sAUTRDA-oiX0Sq0tJLxM8g.JPEG.a_zum_koya%2F20231117%25A3%25DF114135.jpg&type=sc960_832",
			address = "서울 서초구 서초대로38길 12 롯데마트 내",
			geometryFactory.createPoint(Coordinate(127.0061464, 37.4902536)),
		)
		val restaurant2 = Restaurant(
			displayName = "우야 (장어덮밥)",
			name = "우야 서초점",
			thumbnail = "https://d12zq4w4guyljn.cloudfront.net/750_750_20220929055414_photo2_OQsttOHxMH8D.jpg",
			address = "서울특별시 서초구 서초대로42길 41 2층",
			geometryFactory.createPoint(Coordinate(127.0093969, 37.4902836)),
		)
		val restaurant3 = Restaurant(
			displayName = "서초정육식당",
			name = "O파티 불고기 맛집",
			thumbnail = "https://search.pstatic.net/common/?src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20191001_113%2F1569859621119h9hzs_JPEG%2FlX9h7KXb2uoC2VSWwXUuYI_U.jpg",
			address = "서울 서초구 서초대로38길 51",
			geometryFactory.createPoint(Coordinate(127.0068856, 37.4889892)),
		)

		restaurantRepository.saveAll(listOf(restaurant1, restaurant2, restaurant3))
		entityManager.flush()
		entityManager.clear()
	}

	describe("list") {
		context("현재 위치 기준 70m, 340m, 201m 떨어진 식당 3곳이 존재하고, 반경 300m 안에 있는 식당 정보를 조회할 때") {
			it("300m 내에 있는 식당 정보 2곳을 반환한다.") {

				mockApi.get("/api/v1/restaurants") {
					contentType = MediaType.APPLICATION_JSON
					param("latitude", "37.4904845")
					param("longitude", "127.0056469")
					param("rangeMeter", "300")
				}.andExpect {
					status { isOk() }
				}.andDo {
					handle(
						document(
							"{class-name}/list/success/without-displayName",
							responseFields(
								fieldWithPath("restaurants").type(JsonFieldType.ARRAY).description("식당 목록"),
								fieldWithPath("restaurants[].id").type(JsonFieldType.NUMBER).description("식당 id"),
								fieldWithPath("restaurants[].displayName").type(JsonFieldType.STRING).description("식당 이름"),
								fieldWithPath("restaurants[].address").type(JsonFieldType.STRING).description("식당 주소"),
								fieldWithPath("restaurants[].thumbnail").type(JsonFieldType.STRING).description("식당 대표 이미지 url"),
								fieldWithPath("restaurants[].latitude").type(JsonFieldType.NUMBER).description("식당 위치 - 위도"),
								fieldWithPath("restaurants[].longitude").type(JsonFieldType.NUMBER).description("식당 위치 - 경도"),
							)
						)
					)
				}
			}
		}

		context("현재 위치 기준 70m, 340m, 201m 떨어진 식당 3곳이 존재하고, 반경 300m 안의 이름에 '서초'가 포함되는 식당 정보를 조회할 때") {
			it("300m 안에 있는 '서초정육식당' 식당 정보가 반환된다.") {

				mockApi.get("/api/v1/restaurants") {
					contentType = MediaType.APPLICATION_JSON
					param("displayName", "서초")
					param("latitude", "37.4904845")
					param("longitude", "127.0056469")
					param("rangeMeter", "300")
				}.andExpect {
					status { isOk() }
				}.andDo {
					handle(
						document(
							"{class-name}/list/success/with-displayName",
							responseFields(
								fieldWithPath("restaurants").type(JsonFieldType.ARRAY).description("식당 목록"),
								fieldWithPath("restaurants[].id").type(JsonFieldType.NUMBER).description("식당 id"),
								fieldWithPath("restaurants[].displayName").type(JsonFieldType.STRING).description("식당 이름"),
								fieldWithPath("restaurants[].address").type(JsonFieldType.STRING).description("식당 주소"),
								fieldWithPath("restaurants[].thumbnail").type(JsonFieldType.STRING).description("식당 대표 이미지 url"),
								fieldWithPath("restaurants[].latitude").type(JsonFieldType.NUMBER).description("식당 위치 - 위도"),
								fieldWithPath("restaurants[].longitude").type(JsonFieldType.NUMBER).description("식당 위치 - 경도"),
							)
						)
					)
				}
			}
		}

		context("현재 위치 기준 70m, 340m, 201m 떨어진 식당 3곳이 존재하고, 반경 500m 안에 있는 식당 정보를 조회할 때") {
			it("50m 내에 있는 식당 정보가 없어서 빈 리스트를 반환한다.") {

				mockApi.get("/api/v1/restaurants") {
					contentType = MediaType.APPLICATION_JSON
					param("latitude", "37.4904845")
					param("longitude", "127.0056469")
					param("rangeMeter", "50")
				}.andExpect {
					status { isOk() }
				}.andDo {
					handle(
						document(
							"{class-name}/list/success/return-empty-list",
							responseFields(
								fieldWithPath("restaurants").type(JsonFieldType.ARRAY).description("식당 목록"),
							)
						)
					)
				}
			}
		}

		context("현재 위치 기준 70m, 340m, 201m 떨어진 식당 3곳이 존재하고, 검색조건 없이 식당 목록 정보를 조회할 때") {
			it("기본 검색기준(마제스타시티 위치 & 1km 반경)으로 검색하여 결과를 반환한다.") {

				mockApi.get("/api/v1/restaurants") {
					contentType = MediaType.APPLICATION_JSON
				}.andExpect {
					status { isOk() }
				}.andDo {
					handle(
						document(
							"{class-name}/list/success/without-searchData",
							responseFields(
								fieldWithPath("restaurants").type(JsonFieldType.ARRAY).description("식당 목록"),
								fieldWithPath("restaurants[].id").type(JsonFieldType.NUMBER).description("식당 id"),
								fieldWithPath("restaurants[].displayName").type(JsonFieldType.STRING).description("식당 이름"),
								fieldWithPath("restaurants[].address").type(JsonFieldType.STRING).description("식당 주소"),
								fieldWithPath("restaurants[].thumbnail").type(JsonFieldType.STRING).description("식당 대표 이미지 url"),
								fieldWithPath("restaurants[].latitude").type(JsonFieldType.NUMBER).description("식당 위치 - 위도"),
								fieldWithPath("restaurants[].longitude").type(JsonFieldType.NUMBER).description("식당 위치 - 경도"),
							)
						)
					)
				}
			}
		}
	}
})
