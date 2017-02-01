package ee.system

open class SocketServiceController<T : SocketService> : SocketServiceControllerBase<T> {
    companion object {
        val EMPTY = SocketServiceControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

