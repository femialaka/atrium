//TODO remove file with 0.17.0
@file:Suppress("DEPRECATION")

package ch.tutteli.atrium.domain.builders.reporting.impl

import ch.tutteli.atrium.core.coreFactory
import ch.tutteli.atrium.domain.builders.reporting.TranslatorOption
import ch.tutteli.atrium.reporting.translating.Locale
import ch.tutteli.atrium.reporting.translating.LocaleOrderDecider
import ch.tutteli.atrium.reporting.translating.TranslationSupplier
import ch.tutteli.atrium.reporting.translating.Translator

@Deprecated("Will be removed with 0.17.0")
internal class TranslatorOptionImpl(
    override val translationSupplier: TranslationSupplier,
    override val localeOrderDecider: LocaleOrderDecider
) : TranslatorOption {

    override fun withDefaultTranslator(primaryLocale: Locale, vararg fallbackLocales: Locale) =
        ObjectFormatterOptionImpl(
            coreFactory.newTranslator(
                translationSupplier,
                localeOrderDecider,
                primaryLocale,
                fallbackLocales.toList()
            )
        )

    override fun withTranslator(factory: (TranslationSupplier, LocaleOrderDecider) -> Translator) =
        ObjectFormatterOptionImpl(factory(translationSupplier, localeOrderDecider))
}
