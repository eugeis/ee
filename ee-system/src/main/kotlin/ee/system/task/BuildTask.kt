package ee.system.task

import ee.system.dev.BuildRequest
import ee.system.dev.BuildTool
import ee.task.Result
import java.nio.file.Path
import java.nio.file.Paths

open class BuildTask : BuildTaskBase {
    companion object {
        val EMPTY = BuildTaskBase.EMPTY
    }

    constructor(name: String = "", group: String = "", buildTool: BuildTool = BuildTool.EMPTY,
        buildHome: Path = Paths.get(""), buildRequest: BuildRequest = BuildRequest.EMPTY) : super(name, group,
        buildTool, buildHome, buildRequest) {

    }

    override fun execute(output: (String) -> Unit): Result {
        val ret = buildTool.build(buildHome, buildRequest, output)
        ret.action = name
        return ret
    }
}

