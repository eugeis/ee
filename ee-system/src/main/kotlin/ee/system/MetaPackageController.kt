package ee.system

open class MetaPackageController<T : MetaPackage> : MetaPackageControllerBase<T> {
    companion object {
        val EMPTY = MetaPackageControllerBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

