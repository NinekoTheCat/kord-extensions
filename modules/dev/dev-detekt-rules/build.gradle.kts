plugins {
	`kordex-module`
	`published-module`
}

group = "dev.kordex.modules"

metadata {
	name = "KordEx: Detekt Rules"
	description = "KordEx module that provides extra custom detekt rules "
}

dependencies {
	implementation(libs.kotlin.stdlib)
	implementation(project(":kord-extensions"))

	compileOnly(libs.detekt.api)
	compileOnly(libs.detekt.psi.utils)

	detektPlugins(project(":modules:dev:dev-detekt-rules"))
	detektPlugins(libs.detekt)
	detektPlugins(libs.detekt.libraries)
	testImplementation(libs.kotest.assertions.core)
	testImplementation(libs.groovy)  // For logback config
	testImplementation(libs.junit)
	testImplementation(libs.logback)
	testImplementation(libs.detekt.test)
}

dokkaModule {
	moduleName.set("KordEx: Detekt Rules")
}
