package ee.system.srv

open class MySqlServiceController<T : MySqlService> : MySqlServiceControllerBase<T> {
    companion object {
        val EMPTY = MySqlServiceControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

