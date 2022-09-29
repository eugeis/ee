package ee.design.ui

import ee.asm.ClassProcessor
import ee.asm.ClassTree
import ee.asm.packageName
import ee.common.ext.isMac
import ee.common.ext.location
import ee.common.ext.mkdirs
import ee.common.ext.toDotsAsPath
import ee.design.ui.des.toDesEnum
import ee.design.ui.des.toDesWidget
import ee.lang.gen.KotlinContext
import ee.lang.tab
import javafx.application.Application
import org.objectweb.asm.tree.ClassNode
import java.nio.file.Path
import java.nio.file.Paths

data class ClassNodes(val enums: MutableList<ClassNode> = arrayListOf(),
    val widgets: MutableList<ClassNode> = arrayListOf())

fun main(args: Array<String>) {
    val path = Paths.get(if (isMac) "/Users/n/git/ee-gen" else "D:\\views\\git\\ee-gen")

    val jxFile = Application::class.java.location().toFile()
    val item = ClassProcessor(listOf(jxFile), emptyList())

    val classTree = item.genClassTree()
    val classNodes = classTree.toClassNodes(setOf("javafx/scene/control/ButtonType"))

    generateDesKotlin(classNodes, path)
}

private fun generateDesKotlin(classTree: ClassNodes, target: Path) {
    classTree.generateSrcGenUiDes(target, "ee.design.ui")
}

private fun ClassNodes.generateSrcGenUiDes(target: Path, namespace: String, classAsEnums: Set<String> = emptySet()) {
    val pkg = target.resolve("src-gen/main/kotlin/${namespace.toDotsAsPath()}")
    pkg.mkdirs()

    pkg.resolve("EeUiJavaFx.gen").toFile().printWriter().use { out ->
        val context = KotlinContext("ee-design_ui_des", "src-gen/main/kotlin", namespace)

        val buffer = StringBuffer()

        buffer.appendLine("import ee.design.*")
        buffer.appendLine()
        buffer.appendLine("object Shared : Module() {")
        var indent: String = tab

        generateSrcGen(buffer, context, enums) { context ->
            toDesEnum(context, indent)
        }
        generateSrcGen(buffer, context, widgets) { context ->
            toDesWidget(context, indent)
        }
        buffer.appendLine("}")
        out.println(context.complete(buffer.toString()))
    }
}


private fun ClassTree.toClassNodes(classAsEnums: Set<String> = emptySet()): ClassNodes {
    val ret = ClassNodes()
    val controls = filter { it.packageName.startsWith("javafx.scene.control") && !it.name.contains("$") }
    controls.forEach { c ->
        if (c.superName.equals("java/lang/Enum") || classAsEnums.contains(c.name)) {
            ret.enums.add(c)
        } else {
            ret.widgets.add(c)
        }
    }
    return ret
}

private fun <T : ClassNode> generateSrcGen(buffer: StringBuffer, context: KotlinContext, items: Collection<T>,
    generator: T.(KotlinContext) -> String) {
    for (item in items) {
        buffer.appendLine(item.generator(context))
        buffer.appendLine()
    }
}
