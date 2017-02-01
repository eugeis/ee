package ee.system

open class SocketServiceCommands<T : SocketService> : SocketServiceCommandsBase<T> {
    companion object {
        val EMPTY = SocketServiceCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

