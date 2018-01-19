package ee.system

open class MetaPackageCommands<T : MetaPackage> : MetaPackageCommandsBase<T> {
    companion object {
        val EMPTY = MetaPackageCommandsBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

