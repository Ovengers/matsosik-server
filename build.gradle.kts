import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	kotlin("jvm") version "1.9.20"
	kotlin("kapt") version "1.9.21"
	kotlin("plugin.spring") version "1.9.20"
	kotlin("plugin.jpa") version "1.9.20"
	kotlin("plugin.allopen") version "1.6.21"
	kotlin("plugin.noarg") version "1.6.21"

	idea
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

group = "productguild.oparty"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // json mapper

	// test setting
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
	testImplementation("io.kotest:kotest-assertions-core:5.8.0")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// security + jwt
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// datasource
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql:42.6.0")
	implementation("org.liquibase:liquibase-core")
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	implementation("org.hibernate:hibernate-spatial:6.4.1.Final")
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
	kapt("jakarta.annotation:jakarta.annotation-api")
	kapt("jakarta.persistence:jakarta.persistence-api")

	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}

tasks {
	val snippetsDir by extra { file("build/generated-snippets") }

	test {
		outputs.dir(snippetsDir)
	}

	asciidoctor {
		doFirst {
			// Docs 최신화를 위해 기존에 존재하는 문서들은 제거
			delete(file("src/main/resources/static/docs"))
		}
		inputs.dir(snippetsDir)
		dependsOn(test)
		sourceDir("src/docs")
		doLast {
			copy {
				from("build/docs/asciidoc")
				into("src/main/resources/static/docs")
			}
		}
	}

	build {
		dependsOn(asciidoctor)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
