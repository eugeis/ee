package ee.system.srv

open class WildFlyServiceController<T : WildFlyService> : WildFlyServiceControllerBase<T> {
    companion object {
        val EMPTY = WildFlyServiceControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

