package ee.lang.gen.doc

import ee.lang.ItemI

fun <T : ItemI<*>> T.toMarkdownFileNameBase(format: String): String {
    return "${name().capitalize()}${format}"
}
