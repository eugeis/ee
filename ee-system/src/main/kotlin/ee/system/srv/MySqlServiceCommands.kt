package ee.system.srv

open class MySqlServiceCommands<T : MySqlService> : MySqlServiceCommandsBase<T> {
    companion object {
        val EMPTY = MySqlServiceCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

