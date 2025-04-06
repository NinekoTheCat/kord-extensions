/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */
package dev.kordex.core.pagination.group

import dev.kordex.core.annotations.ExperimentalPaginationApi
import dev.kordex.core.i18n.EMPTY_KEY
import dev.kordex.core.i18n.types.Key

@ExperimentalPaginationApi

public val emptyGroup: Group = Group(EMPTY_KEY)

@ExperimentalPaginationApi
public data class Group(
	val displayName: Key,
)

/**
 * returns a new [Group] with the display name set to the value of [Key]
 */
@ExperimentalPaginationApi
public fun Key.toGroup(): Group = Group(displayName = this)
