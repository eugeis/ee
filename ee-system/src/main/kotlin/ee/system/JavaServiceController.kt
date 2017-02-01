package ee.system

open class JavaServiceController<T : JavaService> : JavaServiceControllerBase<T> {
    companion object {
        val EMPTY = JavaServiceControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

