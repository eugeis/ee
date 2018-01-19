package ee.design.comp.gen.sql

import ee.design.comp.CompModule
import ee.design.comp.Entity
import ee.design.comp.Model
import ee.design.gen.sql.SqlContext
import java.nio.file.Path

fun Model.extendForSqlGeneration() {
}

fun Model.generateSqlFiles(target: Path, db: String = "") {
    val pkg = target.resolve("src-gen/main/resources")
    pkg.toFile().mkdirs()

    pkg.resolve("${name.capitalize()}_create_tables.sql").toFile().printWriter().use { out ->
        val context = buildContext(db)
        val buffer = StringBuffer()

        for (module in findDownByType(CompModuleI::class.java)) {
            for (item in module.findDownByType(EntityI::class.java)) {
                buffer.appendln(item.toSqlCreateTable(context))
            }
        }

        for (module in findDownByType(CompModuleI::class.java)) {
            for (item in module.findDownByType(EntityI::class.java)) {
                buffer.appendln(item.toSqlCreateForeignKeys(context))
            }
        }

        val content = context.complete(buffer.toString())
        out.println(content)
        println(content)
    }

    pkg.resolve("${name.capitalize()}_drop_tables.sql").toFile().printWriter().use { out ->
        val context = buildContext(db)
        val buffer = StringBuffer()

        for (module in findDownByType(CompModuleI::class.java)) {
            for (item in module.findDownByType(EntityI::class.java)) {
                buffer.appendln(item.toSqlDropTable(context))
                buffer.appendln()
            }
        }
        val content = context.complete(buffer.toString())
        out.println(content)
        println(content)
    }
}

fun Model.buildContext(namespace: String = this.namespace): SqlContext {
    val context = SqlContext(moduleFolder = artifact, genFolder = "src-gen/main/resources", deleteGenFolder = true,
        namespace = namespace)
    return context
}
