/*
 * Copyrighted (Kord Extensions, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages

import dev.kordex.core.i18n.EMPTY_KEY
import dev.kordex.core.i18n.types.Key

/**
 * Class representing a set of pages in a paginator. You can subclass this to customize it if you wish!
 *
 * @param defaultGroup Default page group, if you have more than one.
 */
public open class Pages(public override var defaultGroup: Key = EMPTY_KEY) : IPages<Int> {
	public override val groups: MutableSet<Key>
		get() = internalGroups.keys
	private val internalGroups: LinkedHashMap<Key, MutableList<Page>> = linkedMapOf()

	override fun isEmpty(): Boolean =
		internalGroups.isEmpty() || internalGroups.any { it.value.isEmpty() }

	override fun pageCountForGroup(group: Key): Int = internalGroups[group]!!.size
	public open fun addPage(page: Page): Unit = addPage(defaultGroup, page)

	public open fun addPage(group: Key, page: Page) {
		internalGroups[group] = internalGroups[group] ?: mutableListOf()

		internalGroups[group]!!.add(page)
	}

	public override fun get(page: Int): Page = get(defaultGroup, page)

	public override fun get(group: Key, page: Int): Page {
		if (internalGroups[group] == null) {
			throw NoSuchElementException("No such group: $group")
		}

		val size = internalGroups[group]!!.size

		if (page > size) {
			throw IndexOutOfBoundsException("Page out of range: $page ($size pages)")
		}

		return internalGroups[group]!![page]
	}

	public override fun validate() {
		require(groups.isNotEmpty()) {
				"Invalid pages supplied: At least one page is required"
		}
	}
}

public interface IPages<I> {
	/** Retrieve the list of groups this instance has. **/
	public val groups: Set<Key>

	public val defaultGroup: Key

	/**
	 * @return `true` if there are no pages.
	 */
	public fun isEmpty(): Boolean

	/**
	 * @return count of pages for [group].
	 */
	public fun pageCountForGroup(group: Key): Int

	/** Retrieve the page at the given index, from the default group. **/
	public operator fun get(page: I): Page

	/** Retrieve the page at the given index, from a given group. **/
	public operator fun get(group: Key, page: I): Page

	/** Check that this Pages object is valid, throwing if it isn't. **/
	public fun validate()
}
