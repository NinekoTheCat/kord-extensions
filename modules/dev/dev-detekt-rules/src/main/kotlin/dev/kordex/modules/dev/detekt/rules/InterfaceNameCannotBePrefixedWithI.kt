/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.modules.dev.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtClass

public class InterfaceNameCannotBePrefixedWithI(config: Config) :
	Rule(config) {
	override val issue: Issue = Issue(
		javaClass.simpleName,
		Severity.Style,
		"An interface name should not be prefixed with I",
		Debt.FIVE_MINS
	)

	override fun visitClass(klass: KtClass) {
		super.visitClass(klass)
		if (klass.nameAsSafeName.isSpecial || klass.nameIdentifier?.parent?.javaClass == null) {
			return
		}

		if (!klass.isInterface()) {
			return
		}
		if (klass.identifierName().removeSurrounding("`").matches(interfaceNameRegex)) {
			report(
				CodeSmell(
					issue,
					Entity.atName(klass),
					message = "Interface names should not be prefixed with I"
				)
			)
		}
	}

	public companion object {
		private val interfaceNameRegex: Regex = "I[A-Z]*".simplePatternToRegex()
	}
}
