package ee.lang.gen.puml.classdiagram

import ee.lang.ItemI

fun <T : ItemI<*>> T.toPumlCdFileNameBase(format: String): String {
    return "${name().capitalize()}${format}"
}
