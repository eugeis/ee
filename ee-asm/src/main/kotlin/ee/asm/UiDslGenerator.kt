package ee.asm

import java.io.File

class DSLGenerator(val sourceDirectory: File, val platformJars: List<File>, val versionJars: List<File>,
    val config: AnkoConfiguration, val classTree: ClassTree? = null) : Runnable {
    override fun run() {
        val classTree = this.classTree ?: ClassProcessor(platformJars, versionJars).genClassTree()
        val generationState = GenerationState(classTree, config)
        val renderer = RenderFacade(generationState)
        Writer(renderer).write()

        if (config.generateMavenArtifact) {
            val sdkVersion = File(sourceDirectory, "version.txt").readText()
            generateMavenArtifact(config.outputDirectory, config.artifactName, sdkVersion)
        }
    }
}