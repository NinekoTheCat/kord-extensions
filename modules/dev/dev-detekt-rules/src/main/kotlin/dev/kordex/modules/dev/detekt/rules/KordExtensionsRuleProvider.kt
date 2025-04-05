/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.modules.dev.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

public class KordExtensionsRuleProvider : RuleSetProvider {
	override val ruleSetId: String = "kord-extensions"
	override fun instance(config: Config): RuleSet {
		return RuleSet(
			ruleSetId,
			listOf(
				InterfaceNameCannotBePrefixedWithI(config),
			),
		)
	}
}
