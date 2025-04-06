/*
 * Copyrighted (Kord Extensions, 2025). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package dev.kordex.core.annotations

import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.TYPEALIAS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * this annotation marks the use of the experimental Pagination API
 * ## *WARNING*
 * Here be dragons, currently this API can change at any time.
 */
@RequiresOptIn
@Target(
	CLASS,
	ANNOTATION_CLASS,
	PROPERTY,
	FIELD,
	LOCAL_VARIABLE,
	VALUE_PARAMETER,
	CONSTRUCTOR,
	FUNCTION,
	PROPERTY_GETTER,
	PROPERTY_SETTER,
	TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class ExperimentalPaginationApi
