package ee.system.srv

import java.nio.file.Path
import java.nio.file.Paths

open class WildFlyTool : WildFlyToolBase {
    companion object {
        val EMPTY = WildFlyToolBase.EMPTY
    }

    constructor(elName: String = "", home: Path = Paths.get(""), bin: Path = Paths.get("")) : super(elName, home, bin) {

    }

    override fun start(config: Path, host: String, port: Int, params: Map<String, String>) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun stop(host: String, managementPort: Int, managementUser: String, managementPassword: String) {
        throw IllegalAccessException("Not implemented yet.")
    }

}

