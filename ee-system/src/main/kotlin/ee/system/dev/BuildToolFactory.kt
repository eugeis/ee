package ee.system.dev

import ee.common.ext.exists
import java.nio.file.Path

open class BuildToolFactory : BuildToolFactoryBase {
    companion object {
        val EMPTY = BuildToolFactoryBase.EMPTY
    }

    constructor(maven: Maven = Maven.EMPTY, gradle: Gradle = Gradle.EMPTY) : super(maven, gradle) {

    }

    override fun buildTool(buildHome: Path): BuildTool {
        if (buildHome.resolve("pom.xml").exists()) {
            return maven
        } else if (buildHome.resolve("build.gradle").exists()) {
            return gradle
        } else {
            return BuildTool.EMPTY
        }
    }
}

