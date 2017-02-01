package ee.system.srv

import ee.system.Service
import java.nio.file.Path
import java.nio.file.Paths

open class WildFlyService : WildFlyServiceBase {
    companion object {
        val EMPTY = WildFlyServiceBase.EMPTY
    }

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(), 
        dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0, home: Path = Paths.get(""), 
        config: Path = Paths.get(""), managementPort: Int = 9990, managementUser: String = "admin", 
        managementPassword: String = "admin", defaultParams: MutableMap<String, String> = hashMapOf(), 
        tool: WildFlyTool = WildFlyTool.EMPTY): super(elName, category, dependsOn, dependsOnMe, host, port, home, config, managementPort, managementUser, managementPassword, 
                defaultParams, tool) {

    }


}

