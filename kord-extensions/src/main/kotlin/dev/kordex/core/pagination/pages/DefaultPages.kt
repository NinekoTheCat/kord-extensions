/*
 * Copyrighted (Kord Extensions, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages

import dev.kordex.core.pagination.group.Group
import dev.kordex.core.pagination.group.emptyGroup

/**
 * Class representing a set of pages in a paginator. You can subclass this to customize it if you wish!
 *
 * @param defaultGroup Default page group, if you have more than one.
 */
public open class DefaultPages(public override var defaultGroup: Group = emptyGroup) :
	MutablePages<Int>, CountablePages<Int> {
	override fun set(group: Group, page: Page) {
		internalGroups[group] = internalGroups[group] ?: mutableListOf()

		internalGroups[group]!!.add(page)
	}

	public override val groups: MutableSet<Group>
		get() = internalGroups.keys
	internal val internalGroups: LinkedHashMap<Group, MutableList<Page>> = linkedMapOf()


	public override fun isEmpty(): Boolean =
		internalGroups.isEmpty() || internalGroups.any { it.value.isEmpty() }

	public override fun pageCountForGroup(group: Group): Int = internalGroups[group]!!.size

	public override fun get(group: Group, page: Int): Page {
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

public interface Pages<I> {
	/** Retrieve the list of groups this instance has. **/
	public val groups: Set<Group>

	public val defaultGroup: Group


	/** Retrieve the page at the given index, from a given group. **/
	public operator fun get(group: Group, page: I): Page

	/** Check that this Pages object is valid, throwing if it isn't. **/
	public fun validate()
}

public interface CountablePages<I> : Pages<I> {
	/**
	 * @return count of pages for [group].
	 * @return the number of pages, or null if this doesn't support counting pages
	 */
	public fun pageCountForGroup(group: Group): Int

	/**
	 * @return `true` if there are no pages.
	 */
	public fun isEmpty(): Boolean
}

public interface MutablePages<I> : Pages<I> {
	public operator fun set(group: Group = defaultGroup, page: Page)
}

public fun MutablePages<*>.addPage(group: Group, page: Page) {
	this[group] = page
}

public operator fun <I> Pages<I>.get(page: I): Page = this[defaultGroup, page]

