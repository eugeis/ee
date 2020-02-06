package ee.asm

import ee.common.ext.location
import javafx.application.Application
import org.junit.jupiter.api.Test

class ClassProcessorTest {
    //@Test
    fun test() {

        val jxFile = Application::class.java.location().toFile()
        val item = ClassProcessor(listOf(jxFile), emptyList())
        val classTree = item.genClassTree().filter {
            it.packageName.startsWith("javafx.scene.control") && !it.name.contains("$")
        }.forEach { c ->
                println(c.name)
                println(" fields:")
                c.fields.forEach { f ->
                    println("  ${f.name}${f.signature.orEmpty()}")
                }
                println(" methods:")
                c.methods.forEach { m ->
                    println("  ${m.name}${m.signature.orEmpty()}")
                }
            }
        println(classTree)
    }
}