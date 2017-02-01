package ee.system

open class JmxServiceController<T : JmxService> : JmxServiceControllerBase<T> {
    companion object {
        val EMPTY = JmxServiceControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

