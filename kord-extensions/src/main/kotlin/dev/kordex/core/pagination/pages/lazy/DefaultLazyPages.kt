/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages.lazy

import dev.kordex.core.annotations.ExperimentalPaginationApi
import dev.kordex.core.pagination.group.Group
import dev.kordex.core.pagination.group.emptyGroup
import dev.kordex.core.pagination.pages.CountablePages
import dev.kordex.core.pagination.pages.Page
import dev.kordex.core.pagination.pages.Pages

/**
 * Class representing a set of pages generated via calls to the appropriate [LazyPageProvider].
 *
 * @param defaultGroup Default page group, if you have more than one.
 */
@ExperimentalPaginationApi
public open class DefaultLazyPages(defaultGroup: Group = emptyGroup) :
	LazyPages<Int, LazyPageProvider<Int>>(defaultGroup) {
	public constructor(defaultGroup: Group = emptyGroup, provider: (page: Int) -> Page) : this(defaultGroup) {
		this.addProvider(pageProvider(provider), defaultGroup)
	}


	/** Retrieve the page at the given index, from a given group.
	 * @throws NoSuchElementException if there isn't a group with the key of [group]
	 * **/
	public override operator fun get(
		group: Group,
		page: Int,
	): Page = providers[group]?.get(page) ?: throw NoSuchElementException(
		"There is no provider for group, $group"
	)

	/** Check that this [DefaultLazyPages] object is valid, throwing if it isn't. **/
	public override fun validate() {
		require(providers.isNotEmpty()) {
			"There should be at least one provider!"
		}
	}
}

/**
 * same as [DefaultLazyPages] but it is countable
 */
@ExperimentalPaginationApi
public open class CountableLazyPages(
	defaultGroup: Group = emptyGroup,
) : CountablePages<Int>, LazyPages<Int, LazyPageProviderWithSize<Int>>(defaultGroup) {
	public constructor(
		defaultGroup: Group = emptyGroup,
		provider: (page: Int) -> Page,
		size: Int,
	) : this(defaultGroup) {
		this.addProvider(pageProviderWithSize(provider, size), defaultGroup)
	}
	public constructor(
		defaultGroup: Group = emptyGroup,
		sizeCalculator: () -> Int,
		provider: (page: Int) -> Page,
	) : this(defaultGroup) {
		this.addProvider(pageProviderWithDynamicSize(provider, sizeCalculator), defaultGroup)
	}

	override fun isEmpty(): Boolean = providers.none { it.value.pageCount() == 0 }

	/** Check that this [CountableLazyPages] object is valid, throwing if it isn't. **/
	override fun validate() {
		require(providers.isNotEmpty()) {
			"There should be at least one provider!"
		}
	}

	/** Retrieve the page at the given index, from a given group.
	 * @throws NoSuchElementException if there isn't a group with the key of [group]
	 * **/
	override fun get(group: Group, page: Int): Page = providers[group]?.get(page) ?: throw NoSuchElementException(
		"There is no provider for group, $group"
	)

	override fun pageCountForGroup(group: Group): Int = (providers[group]!!.pageCount() as Number).toInt()


}

@ExperimentalPaginationApi
public fun LazyPageProvider<Int>.pageCountOrNull(): Int? =
	if (this is LazyPageProviderWithSize<Int>) {
		(this.pageCount() as Number).toInt()
	} else {
		null
	}

@ExperimentalPaginationApi
public abstract class LazyPages<I, P : LazyPageProvider<I>>(override var defaultGroup: Group = emptyGroup) : Pages<I> {
	public constructor(
		defaultGroup: Group = emptyGroup,
		provider: P,
	) : this(defaultGroup) {
		this.addProvider(provider, defaultGroup)
	}

	override val groups: Set<Group>
		get() = providers.keys
	internal val providers: MutableMap<Group, P> = mutableMapOf()

	/** Add a [provider] to the [group]. **/
	public open fun addProvider(
		provider: P,
		group: Group = defaultGroup,
	) {
		providers[group] = provider
	}
}

@ExperimentalPaginationApi
public interface LazyPageProvider<I> {
	/**
	 * @return [Page] on index [page]
	 * @throws NoSuchElementException if the page on index [page] doesn't exist
	 */
	public operator fun get(page: I): Page
}

@ExperimentalPaginationApi
public interface LazyPageProviderWithSize<I> : LazyPageProvider<I> {
	/**
	 * amount of pages that this provider can currently return.
	 */
	public fun pageCount(): Int
}

/**
 * returns a provider that calls the function [f] for [LazyPageProvider.get].
 */
@ExperimentalPaginationApi
public fun <I> pageProvider(f: (page: I) -> Page): LazyPageProvider<I> = object : LazyPageProvider<I> {
	override fun get(page: I): Page = f.invoke(page)
}

/**
 * returns a provider that calls the function [f] for [LazyPageProviderWithSize.get] and has a fixed [size].
 */
@ExperimentalPaginationApi
public fun <I> pageProviderWithSize(
	f: (page: I) -> Page,
	size: Int,
): LazyPageProviderWithSize<I> = pageProviderWithDynamicSize(f) { size }

/**
 * returns a provider that calls the function [f] for [LazyPageProviderWithSize.get]
 * and calls [s] for [LazyPageProviderWithSize.pageCount].
 */
@ExperimentalPaginationApi
public fun <I> pageProviderWithDynamicSize(
	f: (page: I) -> (Page),
	s: () -> Int,
): LazyPageProviderWithSize<I> = object : LazyPageProviderWithSize<I> {
	override fun get(page: I): Page = f.invoke(page)
	override fun pageCount(): Int = s.invoke()
}

/** Add a [LazyPageProviderWithSize] to the [group] from a function. with a fixed [size]**/
@ExperimentalPaginationApi
public fun <I> LazyPages<I, LazyPageProviderWithSize<I>>.addProvider(
	provider: (page: I) -> Page,
	size: Int,
	group: Group = defaultGroup,
) {
	providers[group] = pageProviderWithSize(provider, size)
}

/** Add a [LazyPageProviderWithSize] to the [group] from a function. with a dynamic size from [sizeCalculator]**/
@ExperimentalPaginationApi
public fun <I> LazyPages<I, LazyPageProviderWithSize<I>>.addProvider(
	provider: (page: I) -> Page,
	sizeCalculator: () -> Int,
	group: Group = defaultGroup,
) {
	providers[group] = pageProviderWithDynamicSize(provider, sizeCalculator)
}
