package productguild.oparty.matsosik.global.healthcheck

import mu.KotlinLogging
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class CustomHealthIndicator : HealthIndicator {
	private val log = KotlinLogging.logger {}

	override fun health(): Health {
		log.info { "health-check api requested" }
		// TODO : DB 커넥션도 같이 확인할 수 있도록 추가
		return Health.up().build()
	}
}
