/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.pagination.pages.lazy

import dev.kordex.core.i18n.toKey
import dev.kordex.core.pagination.pages.Page
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun getATestingPage(): Page {
	val randomNumber = Random.nextLong().toString()
	return Page {
		title = "Test Title id: $randomNumber"
		description = "Test Description"
		field {
			name = "Test Field 1"
			value = "Test Value 1"
		}
		field {
			name = "Test Field 2"
			value = "Test Value 2"
		}
		field {
			name = "random"
			value = randomNumber
		}
	}
}

fun getTestingPageList(length: Int = 4): MutableList<Page> {
	val l = mutableListOf<Page>()
	for (i in 0 until length) {
		l.add(getATestingPage())
	}
	return l
}

private class LazyProviderThatCountsExecutionsAndReturnsTestPage(val pages: List<Page> = getTestingPageList()) :
	LazyPageProviderWithSize<Int, Int> {
	var getExecutions = 0u
	var pageCountExecutions = 0u
	override fun get(page: Int): Page {
		getExecutions++
		return pages[page]
	}

	override fun pageCount(): Int {
		pageCountExecutions++
		return pages.size
	}

	fun assertIWasNotCalled() {
		assertEquals(0u, getExecutions, "get should not have been called")
		assertEquals(0u, pageCountExecutions, "pageCount should not have been called")
	}

	fun assertPageCountWasNotCalled() {
		assertEquals(0u, pageCountExecutions, "pageCount should not have been called")
	}
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LazyDefaultPagesTest {
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can add a provider to the default group`() {
		val lazy = LazyPages()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		lazy.addProvider(provider)
		provider.assertIWasNotCalled()
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can add a provider to a custom group`() {
		val lazy = LazyPages()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		lazy.addProvider(provider, "TEST".toKey())
		provider.assertIWasNotCalled()
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can add a provider to the default group and it'll execute the default one`() {
		val lazy = LazyPages()
		val badProvider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		lazy.addProvider(provider)
		lazy.addProvider(badProvider, "TEST".toKey())
		assertDoesNotThrow {
			lazy[1]
		}
		badProvider.assertIWasNotCalled()
		provider.assertPageCountWasNotCalled()
		assertEquals(1u, provider.getExecutions)
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can add a provider to the custom group and it'll execute the right one`() {
		val lazy = LazyPages()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		val badProvider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		val group = "TEST".toKey()
		lazy.addProvider(badProvider)
		lazy.addProvider(provider, group)
		assertDoesNotThrow {
			lazy[group, 1]
		}
		provider.assertPageCountWasNotCalled()
		badProvider.assertIWasNotCalled()
		assertEquals(1u, provider.getExecutions)
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can get a page from a provider`() {
		val lazy = LazyPages()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage(
			getTestingPageList(10)
		)
		val idx = (0 until 10).random()
		val page = provider[idx]
		lazy.addProvider(provider)
		assertEquals(lazy[idx], page)
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can get a pages from a provider without the order changing`() {
		val lazy = LazyPages()
		val list = getTestingPageList(length = (10..23).random())
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage(
			list.toList()
		)
		lazy.addProvider(provider)
		val newList = mutableListOf<Page>()
		for (i in 0 until lazy.pageCountForGroup(lazy.defaultGroup)) {
			newList.add(lazy[i])
		}
		assertContentEquals(list, newList)
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	fun `Can set default group and get right provider for that group`() {
		val lazy = LazyPages()
		val provider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		val badProvider = LazyProviderThatCountsExecutionsAndReturnsTestPage()
		val group = "TEST".toKey()
		lazy.addProvider(badProvider)
		lazy.addProvider(provider, group)
		lazy.defaultGroup = group
		assertDoesNotThrow {
			lazy[group, 1]
		}
		provider.assertPageCountWasNotCalled()
		badProvider.assertIWasNotCalled()
		assertEquals(1u, provider.getExecutions)
	}
}
