@file:Suppress("UNUSED_PARAMETER", "unused")

package ch.tutteli.atrium.api.infix.en_GB

import ch.tutteli.atrium.api.verbs.internal.expect
//TODO 0.19.0 complains we are not in common module but we are. Check if error disappears once we use the default src folder
//import kotlin.js.JsName

class WorstCase {

    val propAndFun: Int = 1
//    @JsName("propFun")
    fun propAndFun(): Int = 1

    fun overloaded(): Int = 1
    fun overloaded(b: Boolean): Int = 1
}


@Suppress(/* requires new type inference */ "RemoveExplicitTypeArguments")
fun testOverloadAmbiguity() {
    expect(WorstCase()) {
        feature { p<Int>(it::propAndFun) }
        feature { f0<Int>(it::propAndFun) }
        feature { f0<Int>(it::overloaded) }
        feature { f1<Boolean, Int>(it::overloaded, true) }
    }
}
