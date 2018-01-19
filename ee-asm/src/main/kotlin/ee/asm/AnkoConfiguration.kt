package ee.asm

import java.io.File

enum class ExternalAnnotation {
    NotNull, GenerateLayout, GenerateView
}

abstract class AnkoConfiguration {
    open val indent: String = "    "

    open val files: MutableSet<File> = hashSetOf()
    open val tunes: MutableSet<Any> = hashSetOf()

    open val generateStaticFiles: Boolean = true

    open var generateImports: Boolean = true
    open var generatePackage: Boolean = true
    open var generateMavenArtifact: Boolean = true

    abstract val artifactName: String

    abstract val generatorOptions: Set<Any>

    abstract val outputPackage: String

    abstract val outputDirectory: File
    abstract val sourceOutputDirectory: File

    abstract val excludedClasses: Set<String>
    abstract val excludedMethods: Set<String>
    abstract val excludedProperties: Set<String>
    abstract val propertiesWithoutGetters: Set<String>

    abstract val annotationManager: AnnotationManager
    abstract val sourceManager: SourceManager
    abstract val templateManager: Any
    abstract val logManager: Any

    operator fun get(option: Any): Boolean = tunes.contains(option) || files.contains(option)

    abstract fun getOutputFile(ankoFile: Any): File

    fun getTargetArtifactType(): Any {
        return Any()
    }
}