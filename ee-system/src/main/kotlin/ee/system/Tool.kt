package ee.system

import java.nio.file.Path
import java.nio.file.Paths

open class Tool : ToolBase {
    companion object {
        val EMPTY = ToolBase.EMPTY
    }

    constructor(elName: String = "", home: Path = Paths.get("")) : super(elName, home) {

    }


}

