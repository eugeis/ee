package ee.system.srv

import ee.system.*
import java.nio.file.Path
import java.nio.file.Paths

abstract class WildFlyServiceBase : SocketService {
    companion object {
        val EMPTY = WildFlyService()
    }
    var home: Path = Paths.get("")
    var config: Path = Paths.get("")
    var managementPort: Int = 9990
    var managementUser: String = "admin"
    var managementPassword: String = "admin"
    var defaultParams: MutableMap<String, String> = hashMapOf()
    var tool: WildFlyTool = WildFlyTool.EMPTY

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(), 
        dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0, home: Path = Paths.get(""), 
        config: Path = Paths.get(""), managementPort: Int = 9990, managementUser: String = "admin", 
        managementPassword: String = "admin", defaultParams: MutableMap<String, String> = hashMapOf(), 
        tool: WildFlyTool = WildFlyTool.EMPTY): super(elName, category, dependsOn, dependsOnMe, host, port) {
        this.home = home
        this.config = config
        this.managementPort = managementPort
        this.managementUser = managementUser
        this.managementPassword = managementPassword
        this.defaultParams = defaultParams
        this.tool = tool
    }



}

fun WildFlyServiceBase?.orEmpty(): WildFlyService {
    return if (this != null) this as WildFlyService else WildFlyServiceBase.EMPTY
}

abstract class MySqlServiceBase : SocketService {
    companion object {
        val EMPTY = MySqlService()
    }
    var home: Path = Paths.get("")
    var config: Path = Paths.get("")
    var defaultUser: String = ""
    var defaultPassword: String = ""
    var tool: MySqlTool = MySqlTool.EMPTY

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(), 
        dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0, home: Path = Paths.get(""), 
        config: Path = Paths.get(""), defaultUser: String = "", defaultPassword: String = "", 
        tool: MySqlTool = MySqlTool.EMPTY): super(elName, category, dependsOn, dependsOnMe, host, port) {
        this.home = home
        this.config = config
        this.defaultUser = defaultUser
        this.defaultPassword = defaultPassword
        this.tool = tool
    }



}

fun MySqlServiceBase?.orEmpty(): MySqlService {
    return if (this != null) this as MySqlService else MySqlServiceBase.EMPTY
}

abstract class MySqlToolBase : Tool {
    companion object {
        val EMPTY = MySqlTool()
    }
    var bin: Path = Paths.get("")

    constructor(elName: String = "", home: Path = Paths.get(""), bin: Path = Paths.get("")): super(elName, home) {
        this.bin = bin
    }


    abstract fun start(defaultsFile: Path = Paths.get(""), params: Map<String, String> = hashMapOf())

    abstract fun stop(user: String = "", password: String = "")

    abstract fun sql(sqlCommand: String = "", user: String = "", password: String = "", schema: String = "")

    abstract fun truncateDb(schema: String = "", sqlCommand: String = "", user: String = "", password: String = "")

}

fun MySqlToolBase?.orEmpty(): MySqlTool {
    return if (this != null) this as MySqlTool else MySqlToolBase.EMPTY
}

abstract class WildFlyToolBase : Tool {
    companion object {
        val EMPTY = WildFlyTool()
    }
    var bin: Path = Paths.get("")

    constructor(elName: String = "", home: Path = Paths.get(""), bin: Path = Paths.get("")): super(elName, home) {
        this.bin = bin
    }


    abstract fun start(config: Path = Paths.get(""), host: String = "", port: Int = 0, params: Map<String, String> = hashMapOf())

    abstract fun stop(host: String = "", managementPort: Int = 0, managementUser: String = "", managementPassword: String = "")

}

fun WildFlyToolBase?.orEmpty(): WildFlyTool {
    return if (this != null) this as WildFlyTool else WildFlyToolBase.EMPTY
}

abstract class WildFlyServiceControllerBase<T : WildFlyService> : SocketServiceController<T> {
    companion object {
        val EMPTY = WildFlyServiceController<WildFlyService>(WildFlyService.EMPTY)
    }

    constructor(item: T): super(item) {

    }



}

fun WildFlyServiceControllerBase<*> ?.orEmpty(): WildFlyServiceController<*>  {
    return if (this != null) this as WildFlyServiceController<*>  else WildFlyServiceControllerBase.EMPTY
}

abstract class WildFlyServiceQueriesBase<T : WildFlyService> : SocketServiceQueries<T> {
    companion object {
        val EMPTY = WildFlyServiceQueries<WildFlyService>()
    }

    constructor(): super() {

    }



}

fun WildFlyServiceQueriesBase<*> ?.orEmpty(): WildFlyServiceQueries<*>  {
    return if (this != null) this as WildFlyServiceQueries<*>  else WildFlyServiceQueriesBase.EMPTY
}

abstract class WildFlyServiceCommandsBase<T : WildFlyService> : SocketServiceCommands<T> {
    companion object {
        val EMPTY = WildFlyServiceCommands<WildFlyService>(WildFlyService.EMPTY)
    }

    constructor(item: T): super(item) {

    }



}

fun WildFlyServiceCommandsBase<*> ?.orEmpty(): WildFlyServiceCommands<*>  {
    return if (this != null) this as WildFlyServiceCommands<*>  else WildFlyServiceCommandsBase.EMPTY
}

abstract class MySqlServiceControllerBase<T : MySqlService> : SocketServiceController<T> {
    companion object {
        val EMPTY = MySqlServiceController<MySqlService>(MySqlService.EMPTY)
    }

    constructor(item: T): super(item) {

    }



}

fun MySqlServiceControllerBase<*> ?.orEmpty(): MySqlServiceController<*>  {
    return if (this != null) this as MySqlServiceController<*>  else MySqlServiceControllerBase.EMPTY
}

abstract class MySqlServiceQueriesBase<T : MySqlService> : SocketServiceQueries<T> {
    companion object {
        val EMPTY = MySqlServiceQueries<MySqlService>()
    }

    constructor(): super() {

    }



}

fun MySqlServiceQueriesBase<*> ?.orEmpty(): MySqlServiceQueries<*>  {
    return if (this != null) this as MySqlServiceQueries<*>  else MySqlServiceQueriesBase.EMPTY
}

abstract class MySqlServiceCommandsBase<T : MySqlService> : SocketServiceCommands<T> {
    companion object {
        val EMPTY = MySqlServiceCommands<MySqlService>(MySqlService.EMPTY)
    }

    constructor(item: T): super(item) {

    }



}

fun MySqlServiceCommandsBase<*> ?.orEmpty(): MySqlServiceCommands<*>  {
    return if (this != null) this as MySqlServiceCommands<*>  else MySqlServiceCommandsBase.EMPTY
}


