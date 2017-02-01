package ee.system.dev

import ee.task.Result
import java.nio.file.Path
import java.nio.file.Paths

open class BuildTool : BuildToolBase {
    companion object {
        val EMPTY = BuildToolBase.EMPTY
    }

    constructor(elName: String = "", home: Path = Paths.get("")) : super(elName, home) {

    }

    override fun buildRequest(): BuildRequest {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun supports(buildHome: Path): Boolean {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun build(buildHome: Path, request: BuildRequest, output: (String) -> Unit): Result {
        throw IllegalAccessException("Not implemented yet.")
    }

}

