package ee.system

open class PackageCommands<T : Package> : PackageCommandsBase<T> {
    companion object {
        val EMPTY = PackageCommandsBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

