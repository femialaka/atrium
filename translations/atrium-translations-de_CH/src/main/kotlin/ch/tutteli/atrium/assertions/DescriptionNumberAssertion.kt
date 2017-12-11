package ch.tutteli.atrium.assertions

import ch.tutteli.atrium.reporting.translating.ISimpleTranslatable

/**
 * Contains the [IBasicAssertion.description]s of the assertion functions which are applicable to [Number].
 */
enum class DescriptionNumberAssertion(override val value: String) : ISimpleTranslatable {
    IS_LESS_THAN("ist weniger als"),
    IS_LESS_OR_EQUALS("ist weniger oder gleich"),
    IS_GREATER_THAN("ist grösser als"),
    IS_GREATER_OR_EQUALS("ist grösser oder gleich"),
}