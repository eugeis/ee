package ee.task

import ee.common.ext.ifElse

open class Result : ResultBase {
    companion object {
        val EMPTY = ResultBase.EMPTY
    }

    override var ok: Boolean = true
        get() {
            return results.find { !it.ok } == null
        }

    constructor(action: String = "", ok: Boolean = true, failure: String = "", info: String = "",
        error: Throwable? = null, results: List<Result> = emptyList()) : super(action, ok, failure, info, error,
        results) {
    }

    override fun toString(): String {
        return "$action\t-> ${ok.ifElse("success", "failed")}"
    }
}

fun Result.print(output: (String) -> Unit, indent: String = "") {
    output("$indent$this")
    results.forEach { it.print(output, "$indent  ") }
}

