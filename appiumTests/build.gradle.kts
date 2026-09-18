plugins {
	kotlin("jvm")
}

kotlin {
	jvmToolchain(17)
}

dependencies {
	testImplementation(libs.java.client)
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
	useJUnitPlatform()
}