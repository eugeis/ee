package ee.system.srv

import ee.system.Service
import java.nio.file.Path
import java.nio.file.Paths

open class MySqlService : MySqlServiceBase {
    companion object {
        val EMPTY = MySqlServiceBase.EMPTY
    }

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(),
        dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0, home: Path = Paths.get(""),
        config: Path = Paths.get(""), defaultUser: String = "", defaultPassword: String = "",
        tool: MySqlTool = MySqlTool.EMPTY) : super(elName, category, dependsOn, dependsOnMe, host, port, home, config,
        defaultUser, defaultPassword, tool) {

    }


}

