package productguild.oparty.matsosik.testconfig

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class ProjectTestConfiguration : AbstractProjectConfig() {
	override fun extensions() = listOf(SpringExtension)
}