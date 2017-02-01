package ee.system

open class ServiceCommands<T : Service> : ServiceCommandsBase<T> {
    companion object {
        val EMPTY = ServiceCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

