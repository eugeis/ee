package ee.system

open class AddonPackageCommands<T : AddonPackage> : AddonPackageCommandsBase<T> {
    companion object {
        val EMPTY = AddonPackageCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

