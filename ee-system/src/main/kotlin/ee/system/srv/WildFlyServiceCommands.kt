package ee.system.srv

open class WildFlyServiceCommands<T : WildFlyService> : WildFlyServiceCommandsBase<T> {
    companion object {
        val EMPTY = WildFlyServiceCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

