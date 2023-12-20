package productguild.oparty.matsosik.users.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(
	name = "users",
	indexes = [
		Index(name = "uk_users_on_email", unique = true, columnList = "email"),
		Index(name = "idx_users_on_email_created_at", columnList = "email, created_at"),
		Index(name = "idx_users_on_name_created_at", columnList = "name, created_at"),
	]
)
@Entity
class User(
	val email: String,
	var password: String,
	var name: String,
	var nickname: String? = null,
	var profileImage: String? = null,
	var createdAt: LocalDateTime = LocalDateTime.now(),
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0

	var updatedAt: LocalDateTime? = null
}