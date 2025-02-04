package ch.tutteli.atrium.logic.creating.transformers

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.core.None
import ch.tutteli.atrium.core.Option
import ch.tutteli.atrium.core.Some
import ch.tutteli.atrium.creating.AssertionContainer
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic._logic
import ch.tutteli.atrium.logic.changeSubject
import ch.tutteli.atrium.reporting.translating.Translatable

/**
 * Defines the contract to change the subject of an assertion container (e.g. the subject of [Expect]) by creating
 * a new [Expect] whereas the new [Expect] delegates assertion checking to a given original assertion container.
 */
interface SubjectChanger {

    /**
     * Changes to a new subject according to the given [transformation] without showing it
     * in reporting and returns a new [Expect] for the new subject.
     *
     * Explained a bit more in depth: it creates a new [Expect] incorporating the given [transformation]
     * whereas the new [Expect] delegates assertion checking to the given [container] -
     * the change as such will not be reflected in reporting.
     *
     * This method is useful if you want to make feature assertion(s) but you do not want that the feature is shown up
     * in reporting. For instance, if a class can behave as another class (e.g. `Sequence::asIterable`) or you want to
     * hide a conversion (e.g. `Int::toChar`) then you can use this function.
     *
     * Notice, in case the change to the new subject is not always safe (you assert so but it does not have to be),
     * then you should use [reported] so that the assertion is reflected in reporting.
     *
     * @param container the assertion container with the current subject (before the change).
     * @param transformation Provides the subject.
     *
     * @return The newly created [Expect] for the extracted feature.
     */
    fun <T, R> unreported(
        container: AssertionContainer<T>,
        transformation: (T) -> R
    ): Expect<R>


    /**
     * Changes to a new subject according to the given [transformation] --
     * the change as such is reflected in reporting by the given [description] and [representation].
     *
     * Explained a bit more in depth: it creates a new [Expect] incorporating the given [transformation]
     * whereas the new [Expect] delegates assertion checking to the given [container].
     * The [transformation] as such can either return the new subject wrapped in a [Some] or return [None] in case
     * the transformation cannot be carried out.
     *
     * This method is useful if you want to change the subject whereas the change as such is assertion like as well, so
     * that it should be reported as well. For instance, say you want to change the subject of type `Int?` to `Int`.
     * Since the subject could also be `null` it makes sense to report this assertion instead of failing
     * with an exception.
     *
     * @param container the assertion container with the current subject (before the change)
     * @param description Describes the kind of subject change (e.g. in case of a type change `is a`).
     * @param representation Representation of the change (e.g. in case of a type transformation the KClass).
     * @param transformation Provides the subject wrapped into a [Some] if the extraction as such can be carried out
     *   otherwise [None].
     * @param failureHandler The [FailureHandler] which shall be used in case the subject cannot be transformed.
     *   A failure has the chance to augment the failing assertion representing the failed transformation with further
     *   information.
     * @param maybeSubAssertions Optionally, subsequent expectations for the new subject. This is especially useful if the
     *   change fails since we can then already show to you (in error reporting) what you wanted to assert about
     *   the new subject (which gives you more context to the error).
     *
     * @return The newly created [Expect] for the extracted feature.
     */
    fun <T, R> reported(
        container: AssertionContainer<T>,
        description: Translatable,
        representation: Any,
        transformation: (T) -> Option<R>,
        failureHandler: FailureHandler<T, R>,
        maybeSubAssertions: Option<Expect<R>.() -> Unit>
    ): Expect<R>

    /**
     * Represents a handler which is responsible to create the assertion resulting from a failed subject change.
     *
     * A handler should augment the failing assertion with explanatory assertions in case the user supplied an
     * assertionCreator lambda. Yet, a failure handler might also add additional information -- e.g. regarding the
     * current subject.
     *
     * @param T The type of the subject
     * @param R The type of the subject after the subject change (if it were possible).
     */
    interface FailureHandler<T, R> {
        /**
         * Creates the failing assertion most likely based on the given [descriptiveAssertion] -- which in turn
         * is based on the previously specified description, representation etc. -- and should incorporate
         * the assertions [maybeAssertionCreator] would have created for the new subject as explanatory assertions.
         *
         * @return A failing assertion.
         */
        fun createAssertion(
            container: AssertionContainer<T>,
            descriptiveAssertion: Assertion,
            maybeAssertionCreator: Option<Expect<R>.() -> Unit>
        ): Assertion
    }
}

/**
 * Represents a [SubjectChanger.FailureHandler] which acts as an adapter for another failure handler by mapping first
 * the given subject to another type [R1] which is understood as input of the other failure handler.
 *
 * Effectively turning a `FailureHandler<R1, R>` into a `FailureHandler<T, R>` with the help of a mapping
 * function `(T) -> R1`
 *
 * @param T The type of the subject
 * @param R1 The type of the mapped subject
 * @param R The type of the subject after the subject change (if it were possible).
 */
class FailureHandlerAdapter<T, R1, R>(
    val failureHandler: SubjectChanger.FailureHandler<R1, R>,
    val map: (T) -> R1
) : SubjectChanger.FailureHandler<T, R> {

    override fun createAssertion(
        container: AssertionContainer<T>,
        descriptiveAssertion: Assertion,
        maybeAssertionCreator: Option<Expect<R>.() -> Unit>
    ): Assertion =
        container.changeSubject.unreported(map).let {
            failureHandler.createAssertion(it._logic, descriptiveAssertion, maybeAssertionCreator)
        }
}



