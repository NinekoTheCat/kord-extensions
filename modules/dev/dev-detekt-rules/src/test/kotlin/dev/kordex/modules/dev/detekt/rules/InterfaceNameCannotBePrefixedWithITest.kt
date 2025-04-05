/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.modules.dev.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveSize
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InterfaceNameCannotBePrefixedWithITest(private val env: KotlinCoreEnvironment) {

	@Test
	fun `reports interface when name is invalid`() {
		val code = """
		 interface ISomething {
		 val IProp
		 }
	 """.trimIndent()
		val findings = InterfaceNameCannotBePrefixedWithI(Config.empty).compileAndLintWithContext(env, code)
		findings shouldHaveSize 1
	}

	@Test
	fun `doesn't flag correct interface name`() {
		val code = """
		 interface Something {
		 val IProp
		 }
	 """.trimIndent()
		val findings = InterfaceNameCannotBePrefixedWithI(Config.empty).compileAndLintWithContext(env, code)
		findings shouldHaveAtMostSize 0
	}
}
