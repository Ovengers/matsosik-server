package productguild.oparty.matsosik.users.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import productguild.oparty.matsosik.testconfig.RestDocsConfiguration

@AutoConfigureRestDocs
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(RestDocsConfiguration::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UsersControllerTest(
	val mockApi: MockMvc,
) : DescribeSpec({
	isolationMode = IsolationMode.InstancePerLeaf

	val mapper = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.withReflectionCacheSize(512)
			.configure(KotlinFeature.NullToEmptyCollection, false)
			.configure(KotlinFeature.NullToEmptyMap, false)
			.configure(KotlinFeature.NullIsSameAsDefault, false)
			.configure(KotlinFeature.StrictNullChecks, false)
			.build()
	)

	describe("#myInfoDetail") {
		context("test용이어서 나중에 수정해야 함") {
			it("수정할거임. 근데 일단응답은 hello") {
				mockApi.get("/api/v1/users/my-info") {
					contentType = MediaType.APPLICATION_JSON
				}.andExpect {
					status { isOk() }
				}.andDo {
					handle(
						document(
							"{class-name}/myInfoDetail/success",
							PayloadDocumentation.responseFields(
								PayloadDocumentation.fieldWithPath("result").type(JsonFieldType.STRING).description("일단 테스트")
							)
						)
					)
				}
			}
		}
	}
})
