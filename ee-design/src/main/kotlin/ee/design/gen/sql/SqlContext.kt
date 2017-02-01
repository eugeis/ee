package ee.design.gen.sql

import ee.common.ext.then
import ee.design.GenerationContext
import ee.design.nL

class SqlContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${namespace.isNotEmpty().then { "use ${namespace}e;" }}$nL$content${toFooter(indent)}"
    }
}