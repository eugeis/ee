package ee.system

open class SocketService : SocketServiceBase {
    companion object {
        val EMPTY = SocketServiceBase.EMPTY
    }

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(), 
        dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0): super(elName, category, dependsOn, dependsOnMe, host, port) {

    }


}

