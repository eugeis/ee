package ee.system

open class JmxServiceCommands<T : JmxService> : JmxServiceCommandsBase<T> {
    companion object {
        val EMPTY = JmxServiceCommandsBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

