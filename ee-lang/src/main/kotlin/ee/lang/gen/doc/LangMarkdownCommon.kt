package ee.lang.gen.doc

import ee.lang.ItemI
import java.util.*

fun <T : ItemI<*>> T.toMarkdownFileNameBase(format: String): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${format}"
}
