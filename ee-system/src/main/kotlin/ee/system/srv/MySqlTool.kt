package ee.system.srv

import java.nio.file.Path
import java.nio.file.Paths

open class MySqlTool : MySqlToolBase {
    companion object {
        val EMPTY = MySqlToolBase.EMPTY
    }

    constructor(elName: String = "", home: Path = Paths.get(""), bin: Path = Paths.get("")): super(elName, home, bin) {

    }

    override fun start(defaultsFile: Path, params: Map<String, String>) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun stop(user: String, password: String) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun sql(sqlCommand: String, user: String, password: String, schema: String) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun truncateDb(schema: String, sqlCommand: String, user: String, password: String) {
        throw IllegalAccessException("Not implemented yet.")
    }

}

