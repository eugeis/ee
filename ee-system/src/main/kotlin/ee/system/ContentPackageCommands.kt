package ee.system

open class ContentPackageCommands<T : ContentPackage> : ContentPackageCommandsBase<T> {
    companion object {
        val EMPTY = ContentPackageCommandsBase.EMPTY
    }
    constructor(item: T): super(item) {

    }


}

