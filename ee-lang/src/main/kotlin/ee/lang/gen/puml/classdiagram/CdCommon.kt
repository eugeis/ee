package ee.lang.gen.puml.classdiagram

import ee.lang.ItemI
import java.util.*

fun <T : ItemI<*>> T.toPumlCdFileNameBase(format: String): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${format}"
}
