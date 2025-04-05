/*
 * Copyrighted (Kord Extensions, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.builders

import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.i18n.EMPTY_KEY
import dev.kordex.core.i18n.types.Key
import dev.kordex.core.pagination.pages.DefaultPages
import dev.kordex.core.pagination.pages.MutablePages
import dev.kordex.core.pagination.pages.Page
import dev.kordex.core.pagination.pages.Pages
import java.util.*

/**
 * Wrapping builder for easily creating paginators using DSL functions defined in the context classes.
 *
 * @param locale Locale to use for the paginator
 * @param defaultGroup Default page group, if any
 */
public class PaginatorBuilder(
	public var locale: Locale? = null,
	public val defaultGroup: Key = EMPTY_KEY,
) {
	// default pages implements Pages<Int>
	// so this is always safe
	/** Pages container object. **/
	@Suppress("UNCHECKED_CAST")
	public val pages: Pages<Page> = DefaultPages(defaultGroup) as Pages<Page>

	/** How many "pages" should be displayed at once, from 1 to 9. **/
	public var chunkedPages: Int = 1

	/** Paginator owner, if only one person should be able to interact. **/
	public var owner: UserBehavior? = null

	/** Paginator timeout, in seconds. When elapsed, the paginator will be destroyed. **/
	public var timeoutSeconds: Long? = null

	/** Whether to keep the paginator content on Discord when the paginator is destroyed. **/
	public var keepEmbed: Boolean = true

	/** Alternative switch button emoji, if needed. **/
	public var switchEmoji: ReactionEmoji? = null

	/** Object containing paginator mutation functions. **/
	public var mutator: PageTransitionCallback? = null

	/** Add a page to [pages], using the default group.
	 * @throws UnsupportedOperationException if [pages] doesn't implement [MutablePages]
	 * **/
	public fun page(page: Page) {
		page(defaultGroup, page)
		if (pages is MutablePages) {
			pages[defaultGroup] = page
		}
	}

	/** Add a page to [pages], using the given group.
	 * @throws UnsupportedOperationException if [pages] doesn't implement [MutablePages]
	 * **/
	public fun page(group: Key, page: Page) {
		if (pages is MutablePages) {
			pages[group] = page
		} else {
			throw UnsupportedOperationException(
				"pages type of ${pages::class.qualifiedName} does not implement MutablePages<Int>"
			)
		}
	}

	/** Add a page to [pages], using the default group.
	 * @throws UnsupportedOperationException if [pages] doesn't implement [MutablePages]
	 * **/
	public fun page(
		builder: suspend EmbedBuilder.() -> Unit,
	): Unit =
		page(Page(builder = builder))

	/** Add a page to [pages], using the given group.
	 * @throws UnsupportedOperationException if [pages] doesn't implement [MutablePages]
	 * **/
	public fun page(
		group: Key,
		builder: suspend EmbedBuilder.() -> Unit,
	): Unit =
		page(group, Page(builder = builder))

	/**
	 * Mutate the paginator and pages, as pages are generated and sent.
	 *
	 * @see PageTransitionCallback
	 */
	public suspend fun mutate(
		body: suspend PageTransitionCallback.() -> Unit,
	) {
		val obj = PageTransitionCallback()

		body(obj)

		this.mutator = obj
	}
}
