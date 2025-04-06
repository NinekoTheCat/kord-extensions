/*
 * Copyrighted (Kord Extensions, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages

import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.annotations.ExperimentalPaginationApi
import dev.kordex.core.i18n.generated.CoreTranslations
import dev.kordex.core.koin.KordExKoinComponent
import dev.kordex.core.pagination.builders.PageMutator
import dev.kordex.core.pagination.group.Group
import dev.kordex.core.utils.capitalizeWords
import dev.kordex.core.utils.textOrNull
import org.koin.core.component.inject
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Representation of a single paginator page. You can extend this to customise it if you wish!
 *
 * @param builder Embed builder callable for building the page's embed
 */
@ExperimentalPaginationApi
public open class Page(
	public open val builder: suspend EmbedBuilder.() -> Unit,
) : KordExKoinComponent {
	/** Current instance of the bot. **/
	public open val bot: ExtensibleBot by inject()

	/** Create an embed builder for this page. **/
	public open suspend fun build(
		mutator: PageMutator? = null,
	): suspend EmbedBuilder.() -> Unit = {
		builder()

		if (mutator != null) {
			mutator(this, this@Page)
		}
	}

	private fun StringBuilder.addPageNumberToFooter(
		chunkSettings: FooterPageNumberSettings,
		locale: Locale,
	) = append(
		if (chunkSettings.isChunked)
			CoreTranslations.Paginator.Footer.Page.chunked
				.withLocale(locale)
				.translate(
					chunkSettings.currentNonChunkedPage,
					chunkSettings.totalNonChunkedPages,
					chunkSettings.totalChunks,
				)
		else CoreTranslations.Paginator.Footer.page
			.withLocale(locale)
			.translate(
				chunkSettings.pageNum + 1,
				chunkSettings.totalChunks
			)
	)

	public data class FooterPageNumberSettings(
		public val totalChunks: Int,
		public val chunkSize: Int,
		public val pageNum: Int,
	) {
		public val isChunked: Boolean = chunkSize > 1
		private val chunkSizeFloat: Float = chunkSize.toFloat()

		public val totalNonChunkedPages: Int
			get() = ceil(totalChunks.div(chunkSizeFloat)).roundToInt()
		public val currentNonChunkedPage: Int
			get() = ceil((pageNum + 1).div(chunkSizeFloat)).roundToInt()


		public fun shouldAddFooter(): Boolean = totalChunks > 1
	}

	@Suppress("LongParameterList")
	/**
	 * adds a footer to the embed
	 * @param footerPageNumberSettings these are the settings for the page number footer,
	 * set to `null` to remove the page numbers entirely
	 * @param builder the embed builder to configure with the footer
	 * @param locale the locale to translate the keys in
	 * @param group the group that the page was assigned to
	 * @param groups the total set of all groups
	 * @param shouldPutFooterInDescription set to true if you want a footer in the [EmbedBuilder.description] and not the
	 * [EmbedBuilder.footer] field
	 */
	public open fun pageFooter(
		builder: EmbedBuilder,
		footerPageNumberSettings: FooterPageNumberSettings? = null,
		locale: Locale,
		group: Group?,
		groups: Collection<Group>,
		shouldPutFooterInDescription: Boolean = false,
	): Unit = builder.run {
		val footerText = buildString {
			if (footerPageNumberSettings?.shouldAddFooter() == true)
				addPageNumberToFooter(footerPageNumberSettings, locale)
			footerText(
				locale,
				group,
				groups
			)
			val currentFooterText = builder.footer?.textOrNull()

			if (currentFooterText?.isEmpty() == true) {
				if (isNotBlank())
						append(" • ")


				append(currentFooterText)
			}
		}

		if (shouldPutFooterInDescription) {
			description = footerText
		} else {
			val currentFooterIcon = footer?.icon

			footer {
				icon = currentFooterIcon

				text = footerText
			}
		}
	}


	private fun StringBuilder.footerText(
		locale: Locale,
		group: Group?,
		groups: Collection<Group>,
	) {
		val groupsSize = groups.size
		val groupIndex = groups.indexOf(group)

		if (group != null || groupsSize > 2) {
			if (isNotBlank())
				append(" • ")


			if (group == null || group.displayName.key.isBlank()) {
				append(
					CoreTranslations.Paginator.Footer.group
						.withLocale(locale)
						.translate(
							groupIndex + 1,
							groupsSize
						)
				)
			} else {
				val groupName = group.displayName
					.withLocale(locale)
					.translate()
					.capitalizeWords(locale)

				append("$groupName (${groupIndex + 1}/${groupsSize})")
			}
		}

	}
}


