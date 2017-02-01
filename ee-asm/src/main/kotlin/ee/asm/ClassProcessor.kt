package ee.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

class ClassProcessor(val platformJars: List<File>, val versionJars: List<File>) {

    fun genClassTree(): ClassTree {
        val classTree = ClassTree()
        for (classData in extractClasses()) {
            classTree.add(processClassData(classData.first), classData.second)
        }
        return classTree
    }

    private fun extractClasses(): Sequence<Pair<InputStream, Boolean>> {
        val hasVersionJars = this.versionJars.isNotEmpty()

        val platformJars = this.platformJars.map { it to hasVersionJars }
        val versionJars = this.versionJars.map { it to false }

        val jarSequences = (platformJars + versionJars).withIndex().asSequence().map { jar ->
            val jarFile = ZipFile(jar.value.first)
            jarFile.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .map { jarFile.getInputStream(it) to jar.value.second }
        }

        return jarSequences.flatten()
    }

    private fun processClassData(classData: InputStream): ClassNode {
        return classData.use {
            val cn = ClassNode()
            val cr = ClassReader(classData)
            cr.accept(cn, 0)
            cn
        }
    }

}