package productguild.oparty.matsosik.global.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
	@Order(1)
	@Bean
	fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
		http.invoke {
			httpBasic { disable() }
			csrf { disable() }
			cors { disable() }
			formLogin { disable() }
			sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

			authorizeRequests {
				authorize("/health-check", permitAll)
				authorize(anyRequest, permitAll) // TODO : api 추가하며 수정해야 함
			}
		}

		return http.build()
	}

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()
}