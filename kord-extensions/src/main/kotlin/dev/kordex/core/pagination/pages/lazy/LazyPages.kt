/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages.lazy

import dev.kordex.core.i18n.EMPTY_KEY
import dev.kordex.core.i18n.types.Key
import dev.kordex.core.pagination.pages.IPages
import dev.kordex.core.pagination.pages.Page

/**
 * Class representing a set of pages generated via calls to the appropriate [LazyPageProvider].
 *
 * @param defaultGroup Default page group, if you have more than one.
 */
public open class LazyPages(public override var defaultGroup: Key = EMPTY_KEY) : IPages<Int> {
	override val groups: Set<Key>
		get() = providers.keys
	private val providers: MutableMap<Key, LazyPageProviderWithSize<Int, Int>> = mutableMapOf()

	public constructor(defaultGroup: Key = EMPTY_KEY, provider: (page: Int) -> Page, size: Int) :
		this(defaultGroup, pageProvider(provider), size)

	public constructor(
		defaultGroup: Key = EMPTY_KEY,
		provider: LazyPageProvider<Int>,
		size: Int,
	) : this(
		defaultGroup,
		object : LazyPageProviderWithSize<Int, Int> {
			override fun get(page: Int): Page = provider[page]
			override fun pageCount(): Int = size
		}
	)

	public constructor(
		defaultGroup: Key = EMPTY_KEY,
		provider: LazyPageProviderWithSize<Int, Int>,
	) : this(defaultGroup) {
		this.addProvider(provider, defaultGroup)
	}

	override fun isEmpty(): Boolean = providers.none { it.value.pageCount() > 0 }
	override fun pageCountForGroup(group: Key): Int = providers[group]!!.pageCount()

	/** Add a [LazyPageProviderWithSize] to the [group]. **/
	public open fun addProvider(
		provider: LazyPageProviderWithSize<Int, Int>,
		group: Key = defaultGroup,
	) {
		providers[group] = provider
	}

	/** Add a [LazyPageProviderWithSize] to the [group] from a function. with a fixed [size]**/
	public open fun addProvider(
		provider: (page: Int) -> Page,
		size: Int,
		group: Key = defaultGroup,
	) {
		providers[group] = pageProviderWithSize(provider, size)
	}

	/** Add a [LazyPageProviderWithSize] to the [group] from a function. with a dynamic size from [sizeCalculator]**/
	public open fun addProvider(
		provider: (page: Int) -> Page,
		sizeCalculator: () -> Int,
		group: Key = defaultGroup,
	) {
		providers[group] = pageProviderWithDynamicSize(provider, sizeCalculator)
	}

	/** Retrieve the page at the given index, from the default group. **/
	public override fun get(page: Int): Page = this[defaultGroup, page]

	/** Retrieve the page at the given index, from a given group.
	 * @throws NoSuchElementException if there isn't a group with the key of [group]
	 * **/
	public override operator fun get(
		group: Key,
		page: Int,
	): Page = providers[group]?.get(page) ?: throw NoSuchElementException(
		"There is no provider for group, $group"
	)

	/** Check that this [LazyPages] object is valid, throwing if it isn't. **/
	public override fun validate() {
		require(providers.isNotEmpty()) {
			"There should be at least one provider!"
		}
	}
}

public interface LazyPageProvider<I> {
	/**
	 * @return [Page] on index [page]
	 * @throws NoSuchElementException if the page on index [page] doesn't exist
	 */
	public operator fun get(page: I): Page
}

public interface LazyPageProviderWithSize<I, S> : LazyPageProvider<I> {
	/**
	 * amount of pages that this provider can currently return.
	 */
	public fun pageCount(): S
}

/**
 * returns a provider that calls the function [f] for [LazyPageProvider.get].
 */
public fun <I> pageProvider(f: (page: I) -> Page): LazyPageProvider<I> = object : LazyPageProvider<I> {
	override fun get(page: I): Page = f.invoke(page)
}

/**
 * returns a provider that calls the function [f] for [LazyPageProviderWithSize.get] and has a fixed [size].
 */
public fun <I, S> pageProviderWithSize(
	f: (page: I) -> Page,
	size: S,
): LazyPageProviderWithSize<I, S> = pageProviderWithDynamicSize(f) { size }

/**
 * returns a provider that calls the function [f] for [LazyPageProviderWithSize.get]
 * and calls [s] for [LazyPageProviderWithSize.pageCount].
 */
public fun <I, S> pageProviderWithDynamicSize(
	f: (page: I) -> (Page),
	s: () -> S,
): LazyPageProviderWithSize<I, S> = object : LazyPageProviderWithSize<I, S> {
	override fun get(page: I): Page = f.invoke(page)
	override fun pageCount(): S = s.invoke()
}
