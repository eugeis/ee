package ee.system

open class JavaServiceCommands<T : JavaService> : JavaServiceCommandsBase<T> {
    companion object {
        val EMPTY = JavaServiceCommandsBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

